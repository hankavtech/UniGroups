package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";

    private ArrayList<UniItem>uniItems;
    private RecyclerView rview;
    private RecyclerViewAdapter radapter;
    private LinearLayoutManager linear_manager;
    private boolean isLoading=false;
    private int visibleThreshold=10;
    private int totalItemCount=0;
    private int lastVisibleItem=0;
    private int page=1;
    private int count=0;
    private int pageCount=0;
    private boolean init=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        getUniData(page);
    }

    private void getUniData(Integer page1){

        OkHttpClient httpClient=new OkHttpClient();
        String url="http://192.168.2.13:8000/android/getunisandunilogos/"+String.valueOf(page1);
        Request request=new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                JSONObject uniObject = null;
                try {
                    uniObject = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JSONObject finalUniObject = uniObject;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            initImageBitmaps(finalUniObject);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }



    private void initImageBitmaps(JSONObject reader) throws IOException, JSONException {
        JSONArray uniArray=reader.getJSONArray("unis");
        System.out.println(uniArray.toString());
        if(page==1 && !init){
            count=reader.getInt("count");
            pageCount= (int) (Math.floor(count/30)+1);
            uniItems=new ArrayList<>();
        }
        if(init && page==1){
            uniItems.clear();
            radapter.notifyDataSetChanged();
        }
        for(int i=0;i<uniArray.length();i++){
            String uniName= (String) uniArray.getJSONArray(i).get(1);
            String uniType= (String) uniArray.getJSONArray(i).get(3);
            String uniImage="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxISEhUSEhIVFhUXFxcXGBYVFxcYHhYVGBkaFxgYFxYZHiggGBonHhgXITEhJSkrLi4uFx8zODMtNyguLisBCgoKDg0OGxAQGy0lHyYrMi0tLS0vLS8tLTAvLS0uNS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIAIMBfwMBIgACEQEDEQH/xAAbAAABBQEBAAAAAAAAAAAAAAAEAAIDBQYBB//EAEkQAAIBAgQDBQUEBwYEBAcAAAECEQADBBIhMQVBUQYTImFxMkKBkaEUI7HBB1JicoLR8DNDkqKy4RUkwvEls9LiFlRjc4OUo//EABoBAAIDAQEAAAAAAAAAAAAAAAACAQMEBQb/xAAxEQACAgEEAAMGBAcBAAAAAAAAAQIRAwQSITETQVEFImFxkfAyobHRFBUjQoHB8VL/2gAMAwEAAhEDEQA/AIf0lrD2XHIGSOmdR8vF9azXZ7iQwuLS7JFq54H/AHGPhb+Fo+E1ue2GHN1SgEsUAH/7GGrzu5gSzGwRIUuPkXB+qg/CufHjn7o1v0PX7zar+8JrCWkOFx5w4JQM0Bgqt3YvBLtuQ+hhoGvJjVz2R4ob+GTMfvLTd3cnmVEBj6rB9Qa1HbfgSXLLOijvHRIiJm2CVPrrHwFW9NFbMvbBKo51zAiYAmCeQ21zGNulKK7gfvbAvAgCVIQnZHViYEfrZhuNQdNadFep9nZHPDb9X+55v2hjUMtL0Q2K5FPilFdAwjYrkU+KUUAMilFPilFQAyK4BT4rsUAMiuxTorsVBIyKUU+KUUANilFOiuxUEjQKcBXQKdFKMciugV0CnAdahggnhnDzefJIUAFmY+6o3NWHB+0uAKXThlDLaJDu6yz6E6E7gwazt/iwt2rgVoe4+QRuLVv2vm5I84rM4E9zbuqp0uPnaesGPxrmaiUsk6/tOlgUccL8z1jsNxnvFXvGDXHF66/UZrgCachlED92u9s+0mBtBUxCs5YgHu4z2s0kNvPL2dZA2NYvsDxFBcZFFzMbRBJU5QUWRDHrBrSnhFjEPba7bUlSpkaElcpWesE/U9a57pttG+NpJMDx/DihuAb2yMw6o3sXAOQOxHIgighWs42g+2YdgBF5L1lp5gEG2PgSfnWTDIWZVYHKzLPUKxWfSRXS0mo3LbJ8nO1WDa90VwdiuRUgFcIrcYxopxFICnCgBoFdy08CnBaVsgjC0itSxXCKWwIGFNAqVhTQtFgJRTwKSrUoFRZKQ1Vp4FdApwFK2WJCAqVFpgqa2KrZYkC4/ifdeFELtEsFPsrtMDU69OhrDY7jN/KoDNbXMx8LMCxO5bWTqD8603aLFrhiyrbBD2wSASI8RBJIOk6ajy35YG/dLksRJkamWI02XrWbMy7GrZ6ff1xdkHYjb0xGGoHB8CVcYzxIGGdz+9dxF/8A6SR8KscSn/M2XnQFV1PNr9gj/SflVra177TUWFM+l7E6fSvMzb8j0S+J5rhV+w48K39leYW2+J+6Y/Elf4j0r1Ncb31m2SwzKWRl80EE9NRlMftVi+2XChfsFhuoOv7PP4jf4GpexHEHupLHXUXPK6kKT/ECDVydr5FbVMD4SMpvWQRCOwAML92jMFA/W8feajprRGWh+IYG5hr9xmjLedshjN7SrfEjdY+81HMmrC/h8kgkHxMJBmR4WmeftCu77Izu3BnH9qYVW9A8Uop8UorvWcQZFKKfFKKLAZFLLT4pRRYEcUop8UqLAbFKKfFKKAGRXYp0UoqAGxXQK7FdAoJOAV2KcBTgKUk4BQ+PIOS0Axe4wCxGhTxzLeH3Rod59TRLNEaEy4QwDpOsmB0j5iqHtDaH2hc13QISoXUq0HQgA7+e8xtti1mbbiko8vr6mzSYt2WLfC7+hUMjLrcIXLIZj1LNtG/snQdDUHEny21KBiHBIYQBocp2lhtsTzqW1w5haclwGdgYJIJgnlMdat8T2c+4tMrKzZipRjkhCA0g89Z1P6w10FcKebJLg7cMWOJWYThGIW0MSXuFQAcuZ4M7aA7DQmOlarAcetoygi8G08Vq7mBJ1BC3SV6aGg8NjcTphmuW0QqqSkGBI0DEbwOvlRFrg2bEi2LgZMufMyIxJEchoRM1knvUuGXx2tcoI7R8dFyzaZLobIzMMy92wKjMA3uuCf1YqHgd5bqd5No3DJYKCjaux9giGXX21PMAidaq+O9l2FwxoJI8IzK0c+RX0ijhgMNbsWDaJGKFyLieOHAZmzgZTqFESOR1rRgz5ITjKXkU5sMJRcV5ltlpRUHDMUbq6owyhfERo5jUqdtx/wBqLK16XHkjkipI89kxuEnFkUUhTiKUUzYp1RUiimrT1FI2SdArhFPAruWksKB2FNAogpTe7osKGKKeK7lroFFjJCFdFICnRStliRwVMrQpaJgExMTHnyqMCnwrAqwlSIIPSlYyMRx7HXLzuoKeZAIJUKD4cx9k5ZAE+0eorJYpncky3KZkzEADTpp8q02Mm1dvBSQdYZ9cqEgbz0Px9Kp2xYsXIZLdyCfCYKwVGvh0nXXxbiseZ+ppxHquISdjruCNwwMg+ulFcJullvBh4hh/F0Jz3mkeUPQOFxa3FFxdVYaH4wfrNPuAhs6mDBUx7ykQQfnNefas7o+10OxrE9msWMPj3tSe7us9seVxWOSfUAr8Vrbg1heNdn7vf3biSBDX1I905/aHmDDelF0yT0Dtmo+xo4jMgS4NN2Urp8QMvoapXYG3agg63fEDM5hbgT1CoD/EPWjuF3Pt+EW2Tl70MDHuOZzr8HBPoaqcJYW3nsbm3cOUyTCxsOgym1p+z8uj7Pk1mil6nP1kV4MvkPiuxTwtdy16uzzJFFdipMtcy0WQRxSipMtdy0WSRZa5lqXLSy1NgRxSinxSiiyBkUop8UgKLJGRSAp+WuhaiwGxTb2IVCs85JP6qqJLHy2HxqYLWO7Z8RDXRaQglRBkCFaZOvM6D5Vk1efwse5dmrSYPFybX0PxfHAGZywCyxXLmUyy5A2+rAREedU13ixJJs2wgIExM9Jk6yfhVfdgS7H4nf4fyFM72RoNOU6fICvNzyyk7fmeihijFUgm9jLpGtyDroPPzJM8/nUCWnbZmOkmAdupihrl9xqmUAmJHPnUn2S8xUidRMAESJ8qTavP8xrJHsMIlnE7Hxa+mtE2MHcI0do6nb5nSmWMPibbCAyFvANJkNoYzCAY58quEGNTHthCwzs6z4QdWC3AB0HiA05VXJ8eQ6/yQWcRj7WqXHIjkSRHw8MUfgO1163dS7dt5mtkmRA3BHIRz5Ch8PdxGsXLT5CRldCTmXQwi6k/TWp7XGkzqmKsEBgYZfFMCcoRxKTtpG9VeJLqvoyzYqtv6ml4XxjC3e9e2VS897vIYkDIVCspI8I8QJBYDeiMFinNx1hsjO2p0AKJb8G+u5PxrOng2FxJzYa6UuKSvhJgMscicyjUag/CuWuI4jCnusQqkS3d3d1DkAZp91oC9I5jnWvS6142o3xd0ZdRo4zTfnRsCtKKktkMAw2ImnZa9NuTVo86406ZEq1Iq08JTwtI2RQ1VqUW6ciVMq1U5DUQG3TTao0JS7qo3DqID3VLu6A7UcWuYXu8ltWDEzmJG3IR+NH8HxP2iyt3JkzT4ZBiDG/So8RXRZ4bqzot0xxRxQAEnQDUk8hUIQMMykMDsQZHzFSpkuANXVFTiyans4MmhzSBQbKfjvBBdt5rdkPdMAGSI92SBvAP89orGP2ftWsqYhCwObWy40ZSdWMHzEakzOgFbvtHxY4RrYLKttozMpBceIBgF39lgZHQ15RxjjocG3aUEZp7y4A7tz9/NkEzsdedY8sl2aIRfRs+wmaHtpcDW1k5WkMh01Gg0JLfLlWmuDnXlWJt3LLrdsuy3F0zdeZDDmCZ0/lW57M9qUxa93cAt4gDVeTj9e31HUbiuI3uto7dUXoNXL2F7tGjdAD5gsNKob19bZt5/wC8fu139vKWAPSQG+Iqys4o5DbbUD2D5SPCfxH+wpGgZluy2OXCY+7hm9gvnT9lgNR8U1/g86L4jbZcYzZYS4kBtNXUkx/hBPwFB9q+BZBcxiGGm049RKkHnyX69avftaXsPadNic0c1lQI+YI+BrboJf1o/My62P8ASl8gHLSy1NkpZa9XuPKkOWu5alyV3LRuCiDLSy1LpMc4mPI/9q7lo3E0Q5aUVLHKm3DlUseQJ+QmjcFDMtNy1NaUlRO8a+uxp2SojNNWglFp0yDLSy1JdhVLHYAkx0GtdC1O5BtfZFlpBaly13LU7goYg1rynipa1ddWE3DcafSdD8RBFes5axvbDCKLrXWAki3ljcxI16nf5Cub7SSeNS9DpezZVkcfUxz4MnvC8yEUwdYLcj0qGSsDL7XPy22q3tIWF2CBNmySWMSACdDzMgUFjLBLowgKAvtMo2ieck+lcNSt1I7TVdBePtRh0MAQwWRzIBn41pO0OHP2bBNbABFm4pK7kKVOvpJ+dVWKk8NBnTvduvxrQcZJt4HCEoSQLwMcg8Df0O1Y8k3Hb8zRCKbfyMlwNybluWJ8fwga1tccoHHA2hM2T/8AztisfwdQLySI1kesVucfb/8AGbZ0M9zpzHgt61OV+hEFzyVXHeztoS9uRNxp1DbgSDmGuv40LgODql3hrP4hdOcjXbOBHprWk7U4pHZ7ahwy3DOVLjalV1GQHTfX1quR/HwtZDZAwJU8w66QdfmBUKa8KPPPNk098vTiiDg3Y5MRbxZFzu3TFOA3IjMyxyPPqKC4bi7oRsPjgzKbly0XaJQ2iAcxjXKxGu4mrpbRbC4+CQVxjNp5XG60VibcjFQoLC9i4zeb2ifoDrVe+27++x9tdffQzshbuLhytyfDcdFJ5qpy/KQflV6ooTgyRZVJk2y1sneShImfkfjRoFet07rDFfA8vqXeaXzOhaeq0lqWQAWJgASSeQppSKaEq1Ki1W4TtBhbmXLd1ZioBVgcwGYyCNBHM6VV8M40zYO8TfU3lzlSNCFJGQwZM6wNOlUuaLY4pMN4TbIx+JGZjKoYLGBosQuw0NWw4jYzsneLmUFm3gBfaJbbTnrWG4c98Z7968FVx4mPvKSCBrsseGNCMo9KrMbxe3MLauMgAgzBMTyJBj1rMs6Nz0z7NF+kfEIbVhldWDMSCCDK9R5T+FXXYfxYK2d9X/1GvPcTxTD3FIu94ogRmDHLBnQiQuvIbzRuD4/3X9jiQAJyoxIUZhB8MjN1E7EULJ71jPH7tHpPFcMDYug7ZG/CqT9HaA4TQD+1f8qpLXavEFShKupRk3UyWnxsQsyAYgGDlpdk+0YwdlrT2y33hIMxvAaYBgACR1OlM8nJChwang2JuPicVacqVtlMkLBAYuIJnX2QfUn0q34hjbeGtG7cnKu+UEn5Vj+AdpsN9txVwsVS4i5C4IB7s3GJMTGh0nXlE1VfpB7TOym2ty0VI0W2cxytlYFpPhOWOu9K5htpjO3XDFuCy1i61w4l2ugsWC27LAFSQSSBJJkjmdtqz3EuzK4PCJibxVrly5lW1mKgJlJzMfak6bRvzmBUYjjDFbYzNKKVGp2kaa8gAB8PKgXxruZ1Y9W1PwqpztjRxs3nb7B9zmZVAm8WVuoZGYg+jL9ayqw7ACVcNIZTBB08QPLcV6N22sLdtovKXb4rYusB8xVBwPs0wxltW2zGfVLNm8w+bAVx90Uvjyzr8/4LPg/Hs7WrGMAFxGLW7oHhuOUa2Af1Xh2+lazLWE7W4REuFTbAtm3bYkSYJEksOQ8xtRHAO05st3GJYlNkvHkOQuHmP2vn1p7vsVfA2/FLXfYfuQQGYi3r+0YB9NfxrM8ItfZMRdwTEts9snmIhl8tTOn7RrTuMuVt4e2f86moMdwy1cunEsHLq5C5WCkDITuxA17wyJ1gCrdPPw8ifxKc8N8HH4De7pd3UYxxO1hx++yKTIkRvTLPECfatZdYkuP5V6H+Nxep57+Bzf8An9CYpTbYkTBHkwginHEHkFP8cf8ATRPEMckPfchVEZtZA2ETGvL500dTGUqi/L9hZaacYXJear8wBMO3eM/hywF5zO/TankCY5xPw/oimdlceuKF1ns3WXPlXu5OUhRGbKwJ3J5jWrC5ZsIUzpiRpDHurwzvpBEK2UbmJjasy18Y3x5mt6Ccq5XRXMhzjSYVj6eJB+dP+ym7NtRJYZZjQFpAzHYc9PI0dZsWi6IDfZmR8wyMmgZDCZ1B3y89p1FG8QvphlCsgJYKBZQxmjY3WA8XPwgQddDvSS9oVu2+v+kPD2fe3d6eXzZRcawzDEKp03aF1kMwynQbaMI30pyasw00IH+UNr86OwGOTETaa0UYEObCtAbKcwew2kMNSU2OuxmRl4dcyXO5yu2cHwyGBKWljKRyKmZiKr02tp+8/tL/AIWanR7l7vf7v/pTY/GEZlOWO8VACwGbZiCToJVWHrViuv6v8LKw8vEpI2ihE4NihcGa22Vrz6own2HDsvzGtaLj2t9j5L/pFXabUOeZ2+Kf6lOqwKGBUubX6FXFLLRAt1NZCjdST1mK6DyHOUQHLWR7dqM6SCSQgEcvE2/zrdYrEqgzFRllRDQNyF3MdaxP6RUOcEKBC2zPSWcaee3zrFrpt4qN2hhWWzLi2We8hZR3dpFAYxsGEDqZoB7qqcseIwBzB1jfTnRtlVBufdlj3K5iSBDw0kE7n0oO7hmJRzbK5gAhLBVMDlpqdJ0rjRhfZ2XKuix4qxGDCd4P7UHKI1kTNafjN7/wzCaE+G6fLwke1zrPXOF97hRBAu5w0EMFyGfejeNfhWq4veR8JYw1sS1sOGgSDmI5AeR0IrNkwyajx0/9lsckU2/voxPCMc1y5bJmJgeI6QOlbfFJPHFEkaWP/Lt1nOF9nr5upnCqATmywBEHoN9K2Fzhznin2sAZAtowSF1UBCJOnuz8afJj54IjP1JOOcX+y3ZQFpZtREicxIIaBy+tUvEeIi9iuHOiFQ3i1OvidZ5davOM28NcdxcuJmLezcRzlbWYNttd+Wm9VL4ZXv4MYZkuCzoxQN4ArgBYfxTHXpWaOKkqTvmy55Lbt8Fx2SsWboxqXSADiXgzGuZ950Px02oexf0xMmR3+LjnzUcvSn8J4LcdMZYa0S1y+7LoDE94UfXSAWU6GRIND8PdlGK8GU97jhqPMD40rTT5++yeGWHZG9mxOKsMoy95duBtfCQ4QA8hIE1om4Ys6uI6CqLs7Y/5vEFP/qhh/wDlkekfnV7dwkmT+Neh0s24dnD1UFv/AAkDYFgfDqKy/bfEPbVACQolm5AxsJO53gehPKdTjsQLVtnOYhYkDXmBt8a8+7a49LrWntsSACjKR7LEzMEbxGo8qbUZKxtWLpsS8VOjPYDCC8xVAzHNAGadJB16c96uVt2cLmlhcuqJInRBrvG/PTy+NMu8St2kt27C5UuQSw9phIzSfdHkNesbVV3cPmuOzGFjSdPAQy89tzXNlmvo6ixV2ScZvXGyNOZmBI09nSRlGwP1qDG4krllD7I5ieh0maeW8Sm33kqV8Su1vQwuXMpBg6bVFxHCOMpdjmKnaNNvn86iMlwDiDNjFIIKN8hUVy4hVgASxAA0P50G2JIJAJY9Mv509LF1pkhANyd+W1XpvzKqIrihVUzDSQ0HbeNqZ/xN10V3I6E/lRg4YisM75lMSdtzvrtpVkncKJAB5ZY9rzY/Cjf6Bsvspxevt11jkNZOn1qbD8JzBmuXMsdBM+hnT1q6t4+2sSinQkRMAnXaRqKrDiC0kgHWdd/OTE/Kl3SZKjFE9nC4ZTAXNyzMSfPRRA+ZorDY1bLQqiRzYH00ywdqDuXGGZlUKCQNAYE8gTQrZmbdpOkn50rjfY90eh9oMXN1bRAAh2U66/cXgQfOtVYcDEHTXNfE/vJZOnyrMdqrIzWLwPhUMrnUwGtsoMDzNaWwVa8HBgMzMDtINm0Nf4lb6da52WDb3L0NKfFMxGP/AOZ4th7CsYazbRwJEG2WZx/g5+lS8R4A74+8Ii3LkGNAFASI+QozgFiOPTGgwhY6bPos/IGtPhcxsqXJLTcDP+t96CSYH0qzNwlQsO3ZguH418LfW1dd+6SBlHiCKxVtBvEhTHLWBXouBt275hgty0xzRowb7tYI61R4nhKd3euKPE4W3rsILBY/y/KoOzeBv4W/3azcs5SbidH7sn7sjYk6fGfOiGS+GTKNdGi4RwG3D9/hMNOc5MtlNbcCCd/FvUvBeCqEIxGGw5fMcuW1b9iBGyDWZofs9hLgF0X0MM5gOVP3UaLK77nU61VpxBMDbZcUi5rtxwoAQ5rQAjMF3USZ561olUbbRRfxL3A4BLFpjireHJDMVItoPBAgRkGszWT7b4tVS9aslMtwqxyaMpGX3R1ydOfmKzeLxrZ5kCNdAo0MRERy5edDYppBfNEkENJAnQZWynWTp8ayPWSUns4vgonKMlTV0b39FmKC2LsIzeOCq5ZByjqQBpVR2l4jiBjmK4l0ZbiBVzQFVgsrk2O+1Wf6H8MFt4gg5pukggnUEKdRtPwqi7WXiOI3BOne2/wSK6GKpRbfoO2+Gj021jz3qZleRauZvAdTmsjwgb89qxXbPHRcxGUsH7tDJ0Ki44GkiVOXT00rZss37JkwLV0wOetrQ+XP4CsN24j7Ri+vc2f9SR+dRHnsd8dAfZl8wsnvGW4brBdZgIobQ/H6nrW/x5wjd5edbbsMxBDBWYoiSoYEGdh8q847KsO8wf8A969/oSvTuIIcuIkyMjECNvuxOvOahqpcffYJ2jMdiOMDFm6HtXLRXVWTE3mUqeUO2jD0qzxtqLhly2i6tlmMo3ygDT0rPfo1sBlxAdQRKkZgCBAPI/Ggsf2zUs4uWLrNGUtZCgFZ8PtPMxE1fp5uGRmfU4/ExpGryAbkD10pmNQhPC2UlkUGJjO4XQczrpXneP4zh7rPcdMXLzAKLCho28Z2y6eRNG2uPWvCyXXGVrbBLnegQjK+U+GNMsjzbzrW9VLlbTFHRpNOy/4nxINfOGX3Ea4xGklUdlA6QVB+Fed9oe0gxbrcAZYAQqBqQus89SSa13ZrD3Tjrd17RhgFz+HKc1tgCDMnN+VYO4GtE2lAhXZS2mqqzLM/L6VlnmcmzbjwxgkiUoXJLjKjQcsE+ktEnlTcJjLuoQwp0IO0b+IE+L0OlLEF4yiZaBoDrP8AtQqOFum2FiNOpJ5k8oqh2+S9JF4tp0ti4qgZnIzZiIyqCwyjQAhgYGm9T2uNYi2NrRH7r6anoRGs/OjOFpb+xEXVZvv2MoDp92untDoDtGtVfaO1bFpO6V1AMNmBE+0wE5z1FVeI7ofYqsmPaDExPgElhIzg8xyYa1JwvE4q7eAaXEN4QxUE7+ItOgiYqj4ZaznKS8b6DMZ57sBFaXsyAuJHiuHwPIWAdoGuYjnRLJSsFDmg8WriKWbCvljUh7ZB05zGbrqTzqsfAOSL2Gt37bAyHt5CNdZENI05bcqn/SW7lLaA5R4i6krLQFiIYyNSY6nyqh/R5jGTEG2GORlMgCdRqCATv/Ooju2eIM63bDdcK7T4tUKYu3dupqM4tnMNPeQGPOfIaUXi+1GAW24a2wa4CSWDIzFl5EgA/OibeJAw91oPhDMAwyqSqyASNTrGum4qi7P8SOL4fiRiILKGWQMoICl1228X4CkWS3bXA2ykXPZ7Er31y93g8YDBdTn7+GzJEyPCRJ6b7TpnxMgawCYGnPeD56Vi+FgDFrkQAC466bSLiwAs6SNdOc9aosTxq5mZjK+NhCs6gMOYH4+pro4MijDkw6jG5O1f39C94p2rF613SLyGdtPWEg9QdT00BrF8ZvKtxEXWBGmus66nfkZMmiMM8IzwviJCzJ1UbD57mRqdDVPibruVdizGDmJ2BzemnL51lzTlklz0asONQiXXDzKqtlZZVEsx0XN/pBJ5x8afwXALfuk3STrEyBBGpMfGB60DwRyDc5K9pRtoT4d4jodashjwt5jlKsy5iNBluwi/FYUaeZqjbUixytFPZCrfxC75bkCddrxg6/sga07i2JZngjSPDAOq6D8QdutK0/3lwndjM6QTOY/nUPG7+ZgCuy6E6aEnaPhoa0R55KXwOvXFUWwgCyql9ASWjl60LdcsxY6kyTuQD5eW1T4fDllVzI0CzPLbaakusveSs6wAVhdCADmFNEhg7WZWcwgxzkzy9DvvXXwmsZiSV0CrO2sanl+VG4TDpleW9mMoOkdDm/lQ64C84lBz1nbyjT1prIobZGjZkOXbNEQT1Eb00qo0zEQQI2n0+NE3cQ72e7CsSploAAEaHnQiYVnHmDBk8tufr9DUEhV+6zOAQAOgjcczlgTqNd9KBayQZY5V/WYHz60bibTqFVWPhHu7AzvM+U0NiWvEeOc2ntHpzMihA7PS+I4ErhzaNq67QIM2gJU6bvI2oXg/aBRZCXbN5Wt+A6KRpsQZ10+s1Ba7D3Gu3UVBaRG0LgqGVtRlbY9NKkPYdQfFirA5GCW+gNUrHXZbv4JOC8QQcRv4gKcqYItDAqfB3jEfhUx7T5cGbls2i49zN4dWDElusakDnpQfZngyWsabYvZ7V3D3ULLKkSrjQHy5/wAqpeN3EuYFwB41uanKo0ClhBGpnxaHarI44S4ZCbs2vAOLLibQVntmQHJUkeNc2ZYbWAQuv7VX2CCtckGQ75tCCNEj8vrXm/6N+HYe+jO5uLkugSLoQAlRK7Sesj9YVqDwTDKitZD94slWZ7jANI1YGFZecRrFUyxRU+Bt7aNFh/E1xLjWyVuqq5dDEqdZiT15V552jttjjcAxFoXbNx0W0w7sMgPtBnYAtpHnptvRfFhib3eZhbDQWV0XXONvFm0kM4noF2im2sNeNxWcWSuVg073MxPtdTDHWY1I86t2QfbK5Rb4o89uNbXMl0FjIBVhl8U+8wE7ajcaVJb4raRShJYN7oB1BHNjqeQmBy03ne2uBqLWIsi3bC3Xs3AMoOU28sqDyUgE+rHrQN/sgzX8RdVkRLyOioAD3YbKQAcwlQV2AGmk0jw4mqtlSwstv0O4S82EvGzdCfeiMyBzGUeYA+VabEdkL1y4brXLRZiCS1hTqNo1MbCqfszeucPw98tlkwwygZQQgWDPUiquz2ivvavEYi5nSzYIPeMJusHZjExqQNI2Ap6XdlvK4N0vDsYHBN62SEZVbujpJUnQNr7IqLFdn7zBnuXbJMAszWoECN/HsNTr0Fec2O1XESSWxBAGxzCeW+kUVc7TY0o4bFsCR4cpU5ZM/q1V4mNf3Ebzcp2avqVKtYBUyPuSYJ3I8QjYCir2DxjFkNyz4gQx7p9QwyxGfTQfWsHb7SYsEk4tyCqwJSQQrEmcvMxTzx/EsgH2kksGDGQSN4ECI0/oVU8+OrUnx390TuXwO8fz4Kzct2ntrcZ1UgC4D4YJYksfDBjzrCYa/wB5dKtdcORMJEAR1IqzvcLuvmZ8UxJ1PgmfrUKdl4bMuJYGN+7O3zqyGu08O5P6P9iqachn2Ux/bXPL2P8A011bb5QTdcSfeFsgiYmQNOtEns1cALLiZgeyyQDpqD4tPWrPF8BvG3aYYgDvrWZg1lSQcx1WW030Iin/AJjifMX7q7bvj8h4YYPHJyb3eQLa45iFuWLffZlUqRnAGUWyWUZgJgSdKquHdn72Oa9dtun3bPcYHMJDXDtAPmdqOxXZJ2AIxEHIBDBj48sPBnYmfnVvg8Dew9u5awtxEFzV2YvIUmQi6ECDPi1JnlUS1mm7U0Kk+mZ3ifDjYuojTnYW2BCkbxy6bQdNjV7hOxJS1ir9w5ri2jcDkQEASdNT4z8YHrNC2+z13vFu3L4Zw6vuxzZSDqSsnQRWs7T3BibIspeZVJl1IMMRliY5QPwqP4zBVb19SaM9wW9lwZhv78+9H92u3SqntMly5a8MsO8EmZGobfoZ51ecPwPdp3OZDmfMAcxjwx68hypPhn1Qd3l1Le0f1oMfnWdTx7t6ki55FVGP7P8AeJcZfZbITMzAkSfP0q47LYsrfLEyAjsSSvsiJknblR1zhjo6undKxGUtL6KxGgWIJzBdfLzoO9w69ZN24XQqLTbZiSdGgggCDBB150PNgndSXIQnykc/SCe9ezqi5VYanecuwC6ehqh7AyuMWCJhtjHunnyrY8YwFvF5Hu5kYLPgVSDmCkQskARl23qs4XwO1h7+fOzIFIIK5DLSBqu+g+tWLUYli8NyVkS4y2aw8XstbfAliL91byoGzECLQbdR0YkehG9Y3hXGDh8NiMOhBzvGcTECQwExoR5cztUh4nYtcTt3CSFS3nXxE5XKlN5nb8azPG8UGuXGtLCFiVgtsdpnn8auxY4cefCYsst2brsnj0+5e66oTcuMWYhRHeAbn0/GstxK7cYESIFxwCBJy6GfDJIOkelDcQ4NetYTD4lbrxdLrkDNCka+GOo1NAJZuv7Vx482J+QrVGNrgrci14niFyWVUsShbN4CJzZfED/DVJcu3FzLqu0qRB5EaHXkKOxDAZVLNrppHkKfjLbMhulg+cvaOdYPgykGVPQ+W3OhQiu2Rukw3h4Jtd5k0BVS+hknOQImRoD5HLpzh0KzA88rankRBH4fGg+CYW61u4LKNcIyDKPFAliSVHw1/a86Ot8C4hIiwF8mZF+jNNVrGrZZOXSDMTgLa2nJYeIyuU6SvignaGAOvIx1NUfFLqggEgDUTM/CJ8qusTwjFW7a97bEm6hhPF4PfDCOenloaprtplxT2e/soApBdzNsgMGyjQyTC/4TVkVGytuRacKfNh0g/rSCNCFJ1BGu80zDBAZ5Lrp7x3iTtVVi8Qtoi2twXQJbOgYCWJJXWPn50MmO35TvSqFWS5mhN6yVJgBN4J138hv8aldQ0Mpy+HUGTsQQREwN9az5x6RG0jXQ0v8AiKyTmPTSf6ihwBSLRT4ySykQfPXTTSYoYkqrAbsdhI8wYihsNxJIk/HQT9amHF7UaCf3gDA8jzpdrG3BGAaEIeWJn3oIOnyFHYm8zW1V7g8I0kR0GpA3qks8TtaiCJ5mNugk1KcdZZYLN6nX4Rmj/tRtDcexcd4glu+6MRBzvqeYYgj01Hz86qipd7RtW3yBw7bgMMrCNdCZYHXpVi6IHZypzkmWJ11MnU+etTW70Eif8x/o86xPM7tGuOHjkpbuBxXuqVUfqtrBPkwPypY5bhwr2PsbO3iuLc1PjgKqwSTMEnXTfYkVdd6Nifx/Om5lPNumpP8AW9EcslyS8SM/2Fwdy1YdWtMrZyQLiMMoygTPPb6edXlxLzblPjP00pw56nfnP0/rpXMoB1b5fWKaWVydtBHGo+Z3CK6GfAT8D+JqTE22uEEj/CFH0nWmjb2z+HykelQ3eJWrej3VXyLDbzqFP0QOPxOHBuDIBppwlzcSDPpSucasRIvJHl/QqH/j1n/5pPmKdX6EceoRdS4y5HGdeYZZB9RQDcDsRHcZRIPhZl1AKzE9CRXLva2wDozv6DT+t6DfttbB8Nu4dfIfQn0p1Cb8hHKHwJX7M4fl3qehQ/lNdHZ+1yvXB/CCdPQgf9q7a7a2Pezr8AY+RqZe2OGJgOw/eB/KleFvuP5C/wBN+gLiOz+kW8VGkDNankQNA351Dhuz95QP+Ztk7khSsmI2gxVuO0aQIhtPdDH4jSo73aYD+7I8zpSLDGmtvffBDxwAP+BXgP7a1096Y/w+vzqHFcHxOX7q7Zzae2WiPOEqz/8AiYc0j1gj6kUHc7XkGAifUaUv8JBv8KIcYAVvhvEhEnCEHfK7g/5kA+tXOO4diLhtlXtAW7KWgGJnRRmJgHcg0zC9oL7iRhwwH6s8/U+VGDjIYZchRj52wZ/ib8qSWDGk1tSHjiQGvBsSdnsa7atv8qcODYmfbs7dT/Kms+L1KXRrsGe3+SEfWqbH4rGnS4bkcshT093lUR0WCXkhZQS8i6PDb4/vbA+Jpo4XfAJF6xO+pMchvGm1Yh77iQO9J/aBPzFEWbDtqbLeoT8jtV38t068kVX8DVtaIGuJwoOvvGd5nQb+XlQ+CtIubvcdZJkkEBtZiQZTTnz895rPjBqdM2VuhH/toqxwo6zctD98b/HlVq0uGKoim/IvFt2PexyiNoAO0coqDHYC1cBCcRAUqwYNbDSGEaQVIjX51QYu1kPt2/4FBFdtW3InvEI6CF+qipWiwLlL9P2Dd8C/w32C2iI2JuHKoWVRlmABO5janfbeHAyGY9c4Ov0rK33XTwpPUMxj50I9wHn6QadaTD6A8jb8jT3uI4IYu1dVPALV1LmhOpKNb6HSG260XjON4FlymzmXpHx1ltaxDEDpr/WtMzjmasWDGvIjezYNx7BraFk2WZFYsqtGhPQzpuaDucawPLCN/iistkE707QD606il1+pDk2EcdxVu7dtPasC2qQSpYnP4gdemgj40ZhuPhE7s4a06i41yHkwWEREbaVVMvnSVfOhxT7C2i9sdpMpLWrFuyxEE2VAJG8SRQuJ7QYlj/bXB5ByCPXLFDYa4iiTadyf2yqxtsPa5+lEMGu6LbCjoiKenvHXnTRpeQrt+ZW4gPcMsWc+ZLH60O2Hj3SPURViFuqYzQB+1P0nX/ejcPYd9M7Hltt1IJ2Hp1prQtFCMKTXPsp/oVrX4CAAxuKBzDEjbTWB1jnQTIi87foA7fQkTS70+htrRnRhfKunCitBiFJMqg3G4C/CJ/qKrcRaYH2fjtRdgV5wwpDCjpVhaw+bdlHmx/rqKLu8KtBSRiEZtIGoG+086htIKZSNhQOVMNgUXcwxU6kafq6iPOmhuc/T/f1+dFAe0XtJ9R/0/wAzSdjHrBPqVnauUq451hWxv+9Hwy1IgmZ5RH0pUqbzF8hl5spAEDQch0rtpAWIIHsjkNN9ulKlSskbaGnzPyprNmzBtQDtApUqsj2LI884q5F0qpgE7DTmelB4glIKkjRuZ5bV2lXRx9Iwz7YfgLQc+MZoYjXoIq2PCbOpyfVvwmlSqJtomCtcnEw6KQFUb9J/Gn4myrbqu/QD8KVKq32WR6YDg8OhuZSojpA/GrY8GsZGbJrp7zfzpUqXJJqXA0EnEyuPthHyrIE7STy6mo3vsjlUYgZZiTvMUqVaVyuTM+GWfBPESWJO3M86tb+FTLsefvN19aVKs+XiRfj6KLFX2V4ViB5Udh8U+aJkdCAeXmKVKia90IP3i2zxJAURMQqjY+lC4XjN8yC8iRuqnfflXaVVRiueB5NkPHDNovswnVfD+EVRcJTvGYPLAERJNKlWmH4CiX4jU4vgGGCFhb1gn2339M0VhcaIeBtAP1/2pUqXTSbTsnOkmqIbJ+n+9Rlj+FcpVoKAxQNNBTLh1A86VKoJFZ/MfWuMxgfGlSoAdcHi+B/CmEUqVADlvMpUAwNT8o/maK79iokzMTtSpU7FOLeZQSDBB0I+PP4CmjFPlHiPPn60qVKSRLiGiMxiJiecVpuEcOtMAxQElmHPauUqqzOlwWYuWWhwVvvAAgAJG2n4Ufw7g9h1QvaVicskidwTXKVY5Sddmikc4lhLaKoW2g9keyuxUkiY8hWJ+zrmjKPbI+E1ylWjC+CrKOXCIQDl5Tz38NU2IUAmORP5fzpUq0IoZ//Z";
            String state= (String) uniArray.getJSONArray(i).get(2);
            UniItem item=new UniItem(uniName,uniType,state,uniImage);
            uniItems.add(item);
        }
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");
        if(radapter==null||page>1){
            initRecyclerView();
        }
        isLoading=false;



    }

    private void initRecyclerView(){
        init=true;
        rview=findViewById(R.id.recycler_view);
        radapter=new RecyclerViewAdapter(uniItems,this);
        rview.setAdapter(radapter);
        linear_manager=new LinearLayoutManager(this);
        rview.setLayoutManager(linear_manager);
        rview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(page<=pageCount && page!=-1) {
                    visibleThreshold = 10;
                    totalItemCount = linear_manager.getItemCount();
                    lastVisibleItem = linear_manager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // Call Load more method here to load next page data
                        // Prevent multiple calls by using a boolean
                        isLoading = true; // boolean to Prevent multiple calls
                        page = page + 1;
                        getUniData(page);
                        Log.d(TAG, "onScrolled: " + String.valueOf(page));


                    }
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater minflater=getMenuInflater();
        minflater.inflate(R.menu.uni_search_menu,menu);
        MenuItem searchItem =menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String uniName) {
                Pattern p = Pattern.compile("^[ A-Za-z]+$");
                Matcher m = p.matcher(uniName);
                boolean b = m.matches();
                if(b==false){
                    return false;
                }

                if(uniName.length()>2){
                    isLoading=true;
                    getFilteredUnisData(uniName);
                }
                if(uniName.length()<=2){
                        if(page==-1) {
                            page=1;
                            isLoading = true;
                            getUniData(1);
                        }
                }
                return false;
            }
        });
        return true;


    }

    public void getFilteredUnisData(String uniName){
        page=-1;
        OkHttpClient httpClient=new OkHttpClient();
        String url="http://192.168.2.10:8000/android/getmatchingunis/"+String.valueOf(uniName);
        Request request=new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                JSONObject uniObject = null;
                try {
                    uniObject = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JSONObject finalUniObject = uniObject;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            uniItems.clear();
                            initImageBitmaps(finalUniObject);
                            radapter.notifyDataSetChanged();
                            isLoading=false;


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }



}