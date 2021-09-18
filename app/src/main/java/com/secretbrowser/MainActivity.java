package com.secretbrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Activity main_activity;

    EditText edturl;
    ImageButton btn_tab, btn_menu;
    ProgressBar webloadingbar;
    WebView webView;
    RelativeLayout errorpage;
    TextView tab_num, errortitle, errorsubtitle1, errorsubtitle2, errorsubtitle3, errormeg;
    int internet_connection, server_connection, weberror = 0;

    int VERSION_CODE = BuildConfig.VERSION_CODE;
    double VERSION = 4.1;
    String VERSION_NAME = BuildConfig.VERSION_NAME;
    int veryear = 2021;
    int vermonth = 2;
    int verday = 22;

    double newver = 1.0;
    int newvercode, newyear, newmonth, newday = 0;
    String newmeg = "";
    Elements contents;
    String contents1;
    Document doc = null;

    //setting 값
    int weberror_retry = 0;
    boolean darkmode, checkver, checkver_error = true;

    String[] permission_list = {
            "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"
    };
    String[] setting_title_list = {
            "웹 페이지 다크모드 설정",
            "탭 그리드 레이아웃 표시 개수",
            "웹 에러 발생시 반복 연결",
            "업데이트 확인",
            "업데이트 확인 실패 표시"
    };
    String[] setting_subtitle_list = {
            "웹 페이지에 다크모드를 적용할수 있습니다 오픈소스 darkreader v4.7.15(2020년2월18일)사용 https://github.com/darkreader/darkreader - Secret browser 4.0.517부터 지원",
            "한 줄당 표시되는 탭의 개수를 설정할 수 있습니다. - Secret browser 4.0.517부터 지원",
            "웹 페이지를 로드 중 문제가 발생하면 해결될 때 까지 계속 다시 연결합니다 모바일 데이터 환경에서는 이 설정을 추천하지 않습니다. - Secret browser 4.0.517부터 지원",
            "앱을 켜거나 메인페이지가 로딩될때 업데이트가 있는지 업데이트 확인 서버에 연결합니다 - Secret browser 4.0.517부터 지원",
            "업데이트 확인에 실패할시 뜨는 창을 표시합니다. - Secret browser 4.0.517부터 지원"
    };
    String[] setting_type_list = {
            "boolean", "int", "int", "boolean", "boolean"
    };

    public static ArrayList<String> tab_list = new ArrayList<>();
    public static int tab = 0;
    String Url = "";

    public ArrayList<String> tablist() {
        return tab_list;
    }
    public ArrayList<String> set_tablist(ArrayList<String> set_tab_list) {
        tab_list = set_tab_list;
        return set_tab_list;
    }
    public String gettab_list(int index) {
        return tab_list.get(index);
    }
    public String addtab_list(String url) {
        tab_list.add(url);
        return url;
    }
    public String addindextab_list(int index, String url) {
        tab_list.add(index, url);
        return url;
    }
    public String settab_list(int index, String url) {
        tab_list.remove(index);
        tab_list.add(index, url);
        return url;
    }
    public int removetab_list(int index) {
        tab_list.remove(index);
        return index;
    }
    public int sizetab_list() {
        return tab_list.size();
    }
    public int gettab() {
        return tab;
    }
    public int settab(int index) {
        tab = index;
        return tab;
    }
    public void web_load() {
        if (sizetab_list() == 0) {
            tab_list.add("https://google.com");
            tab = 0;
        }
        if (tab >= sizetab_list()) {
            tab = sizetab_list() - 1;
        }
        Url = tab_list.get(tab);
        weberror = 0;
        edturl.setText(Url);
        tab_num.setText(sizetab_list() + "");
        webView.loadUrl(Url);
    }
    public String[] setting_title_list() {
        return setting_title_list;
    }
    public String getsetting_title_list(int index) {
        return setting_title_list[index];
    }
    public int getsetting_title_list_length() {
        return setting_title_list.length;
    }
    public String[] setting_subtitle_list() {
        return setting_subtitle_list;
    }
    public String getsetting_subtitle_list(int index) {
        return setting_subtitle_list[index];
    }
    public int getsetting_subtitle_list_length() {
        return setting_subtitle_list.length;
    }
    public String[] setting_type_list() {
        return setting_type_list;
    }
    public String getsetting_type_list(int index) {
        return setting_type_list[index];
    }
    public int getsetting_type_list_length() {
        return setting_type_list.length;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        main_activity = MainActivity.this;
        PreferenceHelper preferenceHelper = new PreferenceHelper();
        SettingData settingdata = new SettingData();
        settingdata.load_setting(MainActivity.this);

        tab = preferenceHelper.getIntPref(MainActivity.this, "tab");
        tab_list = preferenceHelper.getStringArrayPref(MainActivity.this, "tablist");
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Url = intent.getData().toString();
            tab = sizetab_list();
            tab_list.add(Url);
        }

        //setting 값
        darkmode = Boolean.parseBoolean(settingdata.getSetting(0));
        weberror_retry = Integer.parseInt(settingdata.getSetting(2));
        checkver = Boolean.parseBoolean(settingdata.getSetting(3));
        checkver_error = Boolean.parseBoolean(settingdata.getSetting(4));

        edturl = findViewById(R.id.edturl);
        btn_tab = findViewById(R.id.btn_tab);
        tab_num = findViewById(R.id.tab_num);
        btn_menu = findViewById(R.id.btn_addtab);
        webloadingbar = findViewById(R.id.webloadngBar);
        webView = findViewById(R.id.webView);
        errorpage = findViewById(R.id.errorpage);
        errortitle = findViewById(R.id.errortitle);
        errorsubtitle1 = findViewById(R.id.errorsubtitle1);
        errorsubtitle2 = findViewById(R.id.errorsubtitle2);
        errorsubtitle3 = findViewById(R.id.errorsubtitle3);
        errormeg = findViewById(R.id.errormeg);

        webView.setVisibility(View.VISIBLE);
        errorpage.setVisibility(View.INVISIBLE);

        // 인터넷 연결 체크
        if (checkver == true) {
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            if(status == NetworkStatus.TYPE_MOBILE) {
                internet_connection = 1;
            } else if (status == NetworkStatus.TYPE_WIFI) {
                internet_connection = 2;
            } else {
                internet_connection = 0;
            }
            if (internet_connection == 0) {
                if (checkver_error == true) {
                    new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                            .setTitle("업데이트 확인 실패")
                            .setMessage("업데이트 확인에 실패 하였습니다 인터넷 연결을 확인하세요.")
                            .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int sumthin) {

                                }
                            })
                            .show();
                }
            } else if (internet_connection == 1 ||internet_connection == 2){
                checkVer();
            } else {
                if (checkver_error == true) {
                    new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                            .setTitle("알 수 없는 에러발생")
                            .setMessage("인터넷 연결을 확인 하는 중에 알 수 없는 에러가 발생하였습니다 internet_connection : " + internet_connection)
                            .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int sumthin) {

                                }
                            })
                            .show();
                }
            }
        }
        browser();

        // tab 버튼을 눌렀을때
        btn_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceHelper preferenceHelper = new PreferenceHelper();
                preferenceHelper.setStringArrayPref(MainActivity.this, "tablist" ,tab_list);
                Intent intent = new Intent(MainActivity.this, TabActivity.class);
                startActivity(intent);
            }
        });

        edturl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_GO:
                        // 검색 동작
                        Url = edturl.getText().toString();
                        // url에 http,https,javascript,content가 들어가있는지 검사
                        if (Url.startsWith("http:")) {
                            webView.loadUrl(Url);
                        } else if (Url.startsWith("https:")) {
                            webView.loadUrl(Url);
                        } else if (Url.startsWith("javascript:")) {
                            webView.loadUrl(Url);
                        } else if (Url.startsWith("content:")) {
                            webView.loadUrl(Url);
                        } else {
                            webView.loadUrl("http://" + Url);
                        }
                        settab_list(tab, Url);
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        webView.goBack();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SettingData settingdata = new SettingData();
        settingdata.save_setting(MainActivity.this);
        PreferenceHelper preferenceHelper = new PreferenceHelper();
        if (tab > sizetab_list()) {
            tab = sizetab_list();
        }
        preferenceHelper.setIntPref(MainActivity.this, "tab", tab);
        preferenceHelper.setStringArrayPref(MainActivity.this, "tablist" ,tab_list);
    }

    public void checkPermission() {
        // 현재 안드로이드 버전이 6.0 이상이면 메서드를 종료
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list) {
            // 권한 허용 여부 확인
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == android.content.pm.PackageManager.PERMISSION_DENIED) {
                // 권한 허용을 확인하는 창을 띄움
                requestPermissions(permission_list, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(grantResults[0] == 0){
                return;
            }else{
                android.widget.Toast.makeText(getApplicationContext(), "앱 권한을 얻지 못하였습니다", android.widget.Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void checkVer() {
        Toast.makeText(getApplicationContext(), "최신 버전 확인 중...", Toast.LENGTH_LONG).show();
        new AsyncTask() {
            @Override
            public Object doInBackground(Object[] params) {
                try {
                    doc = Jsoup.connect("http://zzz2757.kro.kr/Secretbrowser/secret_internet_ver.html")
                            .timeout(3000)
                            .get();
                    contents = doc.select("ver");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            public void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (doc != null) {
                    server_connection = 1;
                    int cnt = 0;
                    for(Element element: contents) {
                        cnt++;
                        if (cnt == 1) {
                            contents1 = element.text();
                            newver = Double.parseDouble(contents1);
                        } else if (cnt == 2) {
                            contents1 = element.text();
                            newvercode = Integer.parseInt(contents1);
                        } else if (cnt == 3) {
                            contents1 = element.text();
                            newyear = Integer.parseInt(contents1);
                        } else if (cnt == 4) {
                            contents1 = element.text();
                            newmonth = Integer.parseInt(contents1);
                        }else if (cnt == 5) {
                            contents1 = element.text();
                            newday = Integer.parseInt(contents1);
                        }else if (cnt == 6) {
                            contents1 = element.text();
                            newmeg = contents1;
                        }
                        if(cnt >= 7)
                            break;
                    }
                } else {
                    server_connection = 0;
                }
                if (server_connection == 0) {
                    if (checkver_error == true) {
                        new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                .setTitle("업데이트 확인 서버 연결실패")
                                .setMessage("업데이트 확인 서버 연결에 실패하였습니다\n일시적인 현상일 수 있습니다 재시도후에도 안되면 개발자에게 문의 하세요")
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {

                                    }
                                })
                                .show();
                    }
                } else if (server_connection == 1){
                    if (VERSION < newver || VERSION_CODE < newvercode) {
                        new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                .setTitle("업데이트 알림")
                                .setMessage("업데이트가 있습니다 다운로드 해주세요 현재버전 "  + VERSION + "." + VERSION_CODE +  " 최신버전 " + newver + "." + newvercode + "  " + newyear + "년" + newmonth + "월" + newday + "일 업데이트  " + "\n" + newmeg)
                                .setPositiveButton("다운로드", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {
                                        webView.loadUrl("http://zzz2757.kro.kr/Secretbrowser/Download.html");
                                    }
                                })
                                .show();
                    }
                } else {
                    if (checkver_error == true) {
                        new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                .setTitle("알 수 없는 에러발생")
                                .setMessage("서버 연결을 확인 하는 중에 알 수 없는 에러가 발생하였습니다 server_connection : " + server_connection)
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {

                                    }
                                })
                                .show();
                    }
                }
            }
        }.execute();
    }

    public void browser() {
        // 웹설정
        final WebSettings webSet = webView.getSettings();
        webSet.setJavaScriptEnabled(true);
        webSet.setJavaScriptCanOpenWindowsAutomatically(true);
        webSet.setSupportMultipleWindows(true);
        webSet.setAllowFileAccessFromFileURLs(true);
        webSet.setDomStorageEnabled(true);
        webSet.setSupportZoom(true);
        webSet.setBuiltInZoomControls(true);
        webSet.setDisplayZoomControls(false);
        webSet.setDefaultTextEncodingName("utf-8");

        webView.setWebChromeClient(new FullscreenableChromeClient(MainActivity.this) {
            @Override
            public void onProgressChanged(WebView view, int Progress) {
                webloadingbar.setProgress(Progress);
                if (Progress == 100) {
                    Handler Handler = new Handler();
                    Handler.postDelayed(new Runnable()  {
                        public void run() {
                            webloadingbar.setProgress(0);
                        }
                    }, 1000);
                }
            }
            @Override public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(MainActivity.this);
                ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource (WebView view, String url) {
                if (darkmode == true) {
                    darkmode();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                weberror = 0;
                Url = url;
                edturl.setText(Url);
                tab_num.setText(sizetab_list() + "");
                settab_list(tab, Url);
                webView.setVisibility(View.VISIBLE);
                errorpage.setVisibility(View.INVISIBLE);
                if (darkmode == true) {
                    darkmode_load();
                    darkmode();
                } else {
                    webView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Url = url;
                edturl.setText(Url);
                settab_list(tab, Url);
                PreferenceHelper preferenceHelper = new PreferenceHelper();
                preferenceHelper.setIntPref(MainActivity.this, "tab", tab);
                preferenceHelper.setStringArrayPref(MainActivity.this, "tablist" ,tab_list);
                if (darkmode == true) {
                    darkmode();
                }
            }

            // api<23
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String url) {
                if (weberror_retry != 0) {
                    Handler Handler = new Handler();
                    Handler.postDelayed(new Runnable()  {
                        public void run() {
                            web_load();
                        }
                    }, weberror_retry);
                }
                if (weberror == 0) {
                    String error = description.substring(5);
                    webView.setVisibility(View.INVISIBLE);
                    errorpage.setVisibility(View.VISIBLE);
                    if (error.equals("ERR_INTERNET_DISCONNECTED")) {
                        errortitle.setText("인터넷에 연결할 수 없음");
                        errorsubtitle1.setText("인터넷 연결이 없습니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    } else if (error.equals("ERR_NAME_NOT_RESOLVED")) {
                        errortitle.setText("사이트에 연결할 수 없음");
                        errorsubtitle1.setText("입력한 도메인 주소를 찾을수 없습니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인, 도메인 주소 확인, DNS 서버 확인, 바이러스및 인터넷을 제어하는 앱 비활성화");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    } else if (error.equals("ERR_CONNECTION_RESET")) {
                        errortitle.setText("사이트에 연결할 수 없음");
                        errorsubtitle1.setText("연결이 재설정 되었습니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인, HTTPS(암호화된 보안 연결) 취약점을 이용하여 차단했을 수 있음 차단 우회 앱 또는 VPN 등을 사용");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    } else if (error.equals("ERR_CONNECTION_CLOSED")) {
                        errortitle.setText("사이트에 연결할 수 없음");
                        errorsubtitle1.setText("서버와의 연결이 닫혔습니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인, 바이러스및 인터넷을 제어하는 앱 비활성화, 사이트가 현재 연결중인 프로토콜을 사용하는지 확인");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    } else if (error.equals("ERR_CONNECTION_REFUSED")) {
                        errortitle.setText("사이트에 연결할 수 없음");
                        errorsubtitle1.setText("서버에서 연결을 거부했습니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인, 서버에서 방화벽 또는 설정을 제대로 했는지 확인");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    } else if (error.equals("ERR_CONNECTION_TIMED_OUT")) {
                        errortitle.setText("페이지를 사용할 수 없습니다");
                        errorsubtitle1.setText("연결대기 시간이 초과 되었습니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인, 브라우저 데이터 및 캐시 삭제, 연결하려는 서버가 정상적으로 작동중인지 확인");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    } else {
                        errortitle.setText("알 수 없는 에러");
                        errorsubtitle1.setText("알 수 없는 에러 입니다.");
                        errorsubtitle2.setText("다음 방법을 시도해 보세요.");
                        errorsubtitle3.setText("연결 확인, 바이러스및 인터넷을 제어하는 앱 비활성화, 브라우저 재시작, 기기 재시작");
                        errormeg.setText("에러코드 " + errorCode + " 에러 내용 " + error);
                    }
                    weberror = 1;
                }
            }

            public void darkmode_load() {
                webView.loadUrl("javascript:/**\n" +
                        " * Minified by jsDelivr using Terser v3.14.1.\n" +
                        " * Original file: /npm/darkreader@4.7.15/darkreader.js\n" +
                        " * \n" +
                        " * Do NOT use SRI with dynamically generated files! More information: https://www.jsdelivr.com/using-sri-with-dynamic-files\n" +
                        " */\n" +
                        "!function(e,t){\"object\"==typeof exports&&\"undefined\"!=typeof module?t(exports):\"function\"==typeof define&&define.amd?define([\"exports\"],t):t((e=e||self).DarkReader={})}(this,function(e){\"use strict\";var t=function(){return(t=Object.assign||function(e){for(var t,r=1,n=arguments.length;r<n;r++)for(var a in t=arguments[r])Object.prototype.hasOwnProperty.call(t,a)&&(e[a]=t[a]);return e}).apply(this,arguments)};function r(e,t,r,n){return new(r||(r=Promise))(function(a,o){function i(e){try{s(n.next(e))}catch(e){o(e)}}function u(e){try{s(n.throw(e))}catch(e){o(e)}}function s(e){e.done?a(e.value):new r(function(t){t(e.value)}).then(i,u)}s((n=n.apply(e,t||[])).next())})}function n(e,t){var r,n,a,o,i={label:0,sent:function(){if(1&a[0])throw a[1];return a[1]},trys:[],ops:[]};return o={next:u(0),throw:u(1),return:u(2)},\"function\"==typeof Symbol&&(o[Symbol.iterator]=function(){return this}),o;function u(o){return function(u){return function(o){if(r)throw new TypeError(\"Generator is already executing.\");for(;i;)try{if(r=1,n&&(a=2&o[0]?n.return:o[0]?n.throw||((a=n.return)&&a.call(n),0):n.next)&&!(a=a.call(n,o[1])).done)return a;switch(n=0,a&&(o=[2&o[0],a.value]),o[0]){case 0:case 1:a=o;break;case 4:return i.label++,{value:o[1],done:!1};case 5:i.label++,n=o[1],o=[0];continue;case 7:o=i.ops.pop(),i.trys.pop();continue;default:if(!(a=(a=i.trys).length>0&&a[a.length-1])&&(6===o[0]||2===o[0])){i=0;continue}if(3===o[0]&&(!a||o[1]>a[0]&&o[1]<a[3])){i.label=o[1];break}if(6===o[0]&&i.label<a[1]){i.label=a[1],a=o;break}if(a&&i.label<a[2]){i.label=a[2],i.ops.push(o);break}a[2]&&i.ops.pop(),i.trys.pop();continue}o=t.call(e,i)}catch(e){o=[6,e],n=0}finally{r=a=0}if(5&o[0])throw o[1];return{value:o[0]?o[1]:void 0,done:!0}}([o,u])}}}window.chrome||(window.chrome={}),window.chrome.runtime||(window.chrome.runtime={});var a=function(){throw new Error(\"Access to some of your resources was blocked by cross-origin policy\")};if(window.chrome.runtime.sendMessage){var o=window.chrome.runtime.sendMessage;window.chrome.runtime.sendMessage=function(){for(var e=[],t=0;t<arguments.length;t++)e[t]=arguments[t];e[0]&&\"fetch\"===e[0].type&&a(),o.apply(window.chrome.runtime,e)}}else window.chrome.runtime.sendMessage=a;window.chrome.runtime.onMessage||(window.chrome.runtime.onMessage={addListener:Function.prototype});var i={cssFilter:\"cssFilter\",svgFilter:\"svgFilter\",staticTheme:\"staticTheme\",dynamicTheme:\"dynamicTheme\"};function u(e){var t=document.createElement(\"a\");return t.href=e,t}function s(e,t){if(t.match(/^.*?\\/\\//)||t.match(/^data\\:/))return t.startsWith(\"//\")?\"\"+location.protocol+t:t;var r=u(e);if(t.startsWith(\"/\"))return u(r.protocol+\"//\"+r.host+t).href;for(var n,a=r.pathname.split(\"/\").concat(t.split(\"/\")).filter(function(e){return e});(n=a.indexOf(\"..\"))>0;)a.splice(n-1,2);return u(r.protocol+\"//\"+r.host+\"/\"+a.join(\"/\")).href}function c(){for(var e=[],t=0;t<arguments.length;t++)e[t]=arguments[t]}function l(){for(var e=[],t=0;t<arguments.length;t++)e[t]=arguments[t]}function d(e,t){Array.from(e).forEach(function(e){if(e instanceof CSSMediaRule){var r=Array.from(e.media);(r.includes(\"screen\")||r.includes(\"all\")||!r.includes(\"print\")&&!r.includes(\"speech\"))&&d(e.cssRules,t)}else if(e instanceof CSSStyleRule)t(e);else if(e instanceof CSSImportRule)try{d(e.styleSheet.cssRules,t)}catch(e){l(e)}else l(\"CSSRule type not supported\",e)})}function f(e,t){Array.from(e).forEach(function(r){var n=e.getPropertyValue(r).trim();n&&t(r,n)})}function h(e){return e.startsWith(\"--\")&&!e.startsWith(\"--darkreader\")}function p(e){var t=new Map;return f(e.style,function(e,r){h(e)&&t.set(e,r)}),t}var m=/url\\((('.+?')|(\".+?\")|([^\\)]*?))\\)/g,g=/@import (url\\()?(('.+?')|(\".+?\")|([^\\)]*?))\\)?;?/g;function v(e){return e.replace(/^url\\((.*)\\)$/,\"$1\").replace(/^\"(.*)\"$/,\"$1\").replace(/^'(.*)'$/,\"$1\")}function b(e){var t=u(e);return t.protocol+\"//\"+t.host+t.pathname.replace(/\\?.*$/,\"\").replace(/(\\/)([^\\/]+)$/i,\"$1\")}var y=/\\/\\*[\\s\\S]*?\\*\\//g;var w=/@font-face\\s*{[^}]*}/g;var k=/var\\((--[^\\s,]+),?\\s*([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\s*)\\)/g;function x(e,t){var r=!1,n=e.replace(k,function(e,n,a){return t.has(n)?t.get(n):a||(l(\"Variable \"+n+\" not found\"),r=!0,e)});return r?n:n.match(k)?x(n,t):n}function S(e){var t=e.h,r=e.s,n=e.l,a=e.a,o=void 0===a?1:a;if(0===r){var i=[n,n,n].map(function(e){return Math.round(255*e)}),u=i[0],s=i[1];return{r:u,g:i[2],b:s,a:o}}var c=(1-Math.abs(2*n-1))*r,l=c*(1-Math.abs(t/60%2-1)),d=n-c/2,f=(t<60?[c,l,0]:t<120?[l,c,0]:t<180?[0,c,l]:t<240?[0,l,c]:t<300?[l,0,c]:[c,0,l]).map(function(e){return Math.round(255*(e+d))});return{r:f[0],g:f[1],b:f[2],a:o}}function E(e,t){void 0===t&&(t=0);var r=e.toFixed(t);if(0===t)return r;var n=r.indexOf(\".\");if(n>=0){var a=r.match(/0+$/);if(a)return a.index===n+1?r.substring(0,n):r.substring(0,a.index)}return r}var A=/^rgba?\\([^\\(\\)]+\\)$/,M=/^hsla?\\([^\\(\\)]+\\)$/,L=/^#[0-9a-f]+$/i;function P(e){var t,r,n,a,o,i=e.trim().toLowerCase();if(i.match(A))return t=R(i,C,T,j),r=t[0],n=t[1],a=t[2],o=t[3],{r:r,g:n,b:a,a:void 0===o?1:o};if(i.match(M))return function(e){var t=R(e,O,q,W),r=t[0],n=t[1],a=t[2],o=t[3];return S({h:r,s:n,l:a,a:void 0===o?1:o})}(i);if(i.match(L))return function(e){var t=e.substring(1);switch(t.length){case 3:case 4:var r=[0,1,2].map(function(e){return parseInt(\"\"+t[e]+t[e],16)}),n=r[0],a=r[1],o=r[2],i=3===t.length?1:parseInt(\"\"+t[3]+t[3],16)/255;return{r:n,g:a,b:o,a:i};case 6:case 8:var u=[0,2,4].map(function(e){return parseInt(t.substring(e,e+2),16)}),n=u[0],a=u[1],o=u[2],i=6===t.length?1:parseInt(t.substring(6,8),16)/255;return{r:n,g:a,b:o,a:i}}throw new Error(\"Unable to parse \"+e)}(i);if(F.has(i))return function(e){var t=F.get(e);return{r:t>>16&255,g:t>>8&255,b:t>>0&255,a:1}}(i);if($.has(i))return function(e){var t=$.get(e);return{r:t>>16&255,g:t>>8&255,b:t>>0&255,a:1}}(i);if(\"transparent\"===e)return{r:0,g:0,b:0,a:0};throw new Error(\"Unable to parse \"+e)}function R(e,t,r,n){var a=e.split(t).filter(function(e){return e}),o=Object.entries(n);return a.map(function(e){return e.trim()}).map(function(e,t){var n,a=o.find(function(t){var r=t[0];return e.endsWith(r)});return n=a?parseFloat(e.substring(0,e.length-a[0].length))/a[1]*r[t]:parseFloat(e),r[t]>1?Math.round(n):n})}var C=/rgba?|\\(|\\)|\\/|,|\\s/gi,T=[255,255,255,1],j={\"%\":100};var O=/hsla?|\\(|\\)|\\/|,|\\s/gi,q=[360,1,1,1],W={\"%\":100,deg:360,rad:2*Math.PI,turn:1};var F=new Map(Object.entries({aliceblue:15792383,antiquewhite:16444375,aqua:65535,aquamarine:8388564,azure:15794175,beige:16119260,bisque:16770244,black:0,blanchedalmond:16772045,blue:255,blueviolet:9055202,brown:10824234,burlywood:14596231,cadetblue:6266528,chartreuse:8388352,chocolate:13789470,coral:16744272,cornflowerblue:6591981,cornsilk:16775388,crimson:14423100,cyan:65535,darkblue:139,darkcyan:35723,darkgoldenrod:12092939,darkgray:11119017,darkgrey:11119017,darkgreen:25600,darkkhaki:12433259,darkmagenta:9109643,darkolivegreen:5597999,darkorange:16747520,darkorchid:10040012,darkred:9109504,darksalmon:15308410,darkseagreen:9419919,darkslateblue:4734347,darkslategray:3100495,darkslategrey:3100495,darkturquoise:52945,darkviolet:9699539,deeppink:16716947,deepskyblue:49151,dimgray:6908265,dimgrey:6908265,dodgerblue:2003199,firebrick:11674146,floralwhite:16775920,forestgreen:2263842,fuchsia:16711935,gainsboro:14474460,ghostwhite:16316671,gold:16766720,goldenrod:14329120,gray:8421504,grey:8421504,green:32768,greenyellow:11403055,honeydew:15794160,hotpink:16738740,indianred:13458524,indigo:4915330,ivory:16777200,khaki:15787660,lavender:15132410,lavenderblush:16773365,lawngreen:8190976,lemonchiffon:16775885,lightblue:11393254,lightcoral:15761536,lightcyan:14745599,lightgoldenrodyellow:16448210,lightgray:13882323,lightgrey:13882323,lightgreen:9498256,lightpink:16758465,lightsalmon:16752762,lightseagreen:2142890,lightskyblue:8900346,lightslategray:7833753,lightslategrey:7833753,lightsteelblue:11584734,lightyellow:16777184,lime:65280,limegreen:3329330,linen:16445670,magenta:16711935,maroon:8388608,mediumaquamarine:6737322,mediumblue:205,mediumorchid:12211667,mediumpurple:9662683,mediumseagreen:3978097,mediumslateblue:8087790,mediumspringgreen:64154,mediumturquoise:4772300,mediumvioletred:13047173,midnightblue:1644912,mintcream:16121850,mistyrose:16770273,moccasin:16770229,navajowhite:16768685,navy:128,oldlace:16643558,olive:8421376,olivedrab:7048739,orange:16753920,orangered:16729344,orchid:14315734,palegoldenrod:15657130,palegreen:10025880,paleturquoise:11529966,palevioletred:14381203,papayawhip:16773077,peachpuff:16767673,peru:13468991,pink:16761035,plum:14524637,powderblue:11591910,purple:8388736,rebeccapurple:6697881,red:16711680,rosybrown:12357519,royalblue:4286945,saddlebrown:9127187,salmon:16416882,sandybrown:16032864,seagreen:3050327,seashell:16774638,sienna:10506797,silver:12632256,skyblue:8900331,slateblue:6970061,slategray:7372944,slategrey:7372944,snow:16775930,springgreen:65407,steelblue:4620980,tan:13808780,teal:32896,thistle:14204888,tomato:16737095,turquoise:4251856,violet:15631086,wheat:16113331,white:16777215,whitesmoke:16119285,yellow:16776960,yellowgreen:10145074})),$=new Map(Object.entries({ActiveBorder:3906044,ActiveCaption:0,AppWorkspace:11184810,Background:6513614,ButtonFace:16777215,ButtonHighlight:15329769,ButtonShadow:10461343,ButtonText:0,CaptionText:0,GrayText:8355711,Highlight:11720703,HighlightText:0,InactiveBorder:16777215,InactiveCaption:16777215,InactiveCaptionText:0,InfoBackground:16514245,InfoText:0,Menu:16185078,MenuText:16777215,Scrollbar:11184810,ThreeDDarkShadow:0,ThreeDFace:12632256,ThreeDHighlight:16777215,ThreeDLightShadow:16777215,ThreeDShadow:0,Window:15527148,WindowFrame:11184810,WindowText:0,\"-webkit-focus-ring-color\":15046400}).map(function(e){var t=e[0],r=e[1];return[t.toLowerCase(),r]}));function B(e,t,r,n,a){return(e-t)*(a-n)/(r-t)+n}function N(e,t,r){return Math.min(r,Math.max(t,e))}function I(e,t){for(var r=[],n=0;n<e.length;n++){r[n]=[];for(var a=0;a<t[0].length;a++){for(var o=0,i=0;i<e[0].length;i++)o+=e[n][i]*t[i][a];r[n][a]=o}}return r}function U(e,t,r){void 0===r&&(r=0);for(var n,a=[];n=e.exec(t);)a.push(n[r]);return a}function z(e){var t=V.identity();return 0!==e.sepia&&(t=I(t,V.sepia(e.sepia/100))),0!==e.grayscale&&(t=I(t,V.grayscale(e.grayscale/100))),100!==e.contrast&&(t=I(t,V.contrast(e.contrast/100))),100!==e.brightness&&(t=I(t,V.brightness(e.brightness/100))),1===e.mode&&(t=I(t,V.invertNHue())),t}var H,V={identity:function(){return[[1,0,0,0,0],[0,1,0,0,0],[0,0,1,0,0],[0,0,0,1,0],[0,0,0,0,1]]},invertNHue:function(){return[[.333,-.667,-.667,0,1],[-.667,.333,-.667,0,1],[-.667,-.667,.333,0,1],[0,0,0,1,0],[0,0,0,0,1]]},brightness:function(e){return[[e,0,0,0,0],[0,e,0,0,0],[0,0,e,0,0],[0,0,0,1,0],[0,0,0,0,1]]},contrast:function(e){var t=(1-e)/2;return[[e,0,0,0,t],[0,e,0,0,t],[0,0,e,0,t],[0,0,0,1,0],[0,0,0,0,1]]},sepia:function(e){return[[.393+.607*(1-e),.769-.769*(1-e),.189-.189*(1-e),0,0],[.349-.349*(1-e),.686+.314*(1-e),.168-.168*(1-e),0,0],[.272-.272*(1-e),.534-.534*(1-e),.131+.869*(1-e),0,0],[0,0,0,1,0],[0,0,0,0,1]]},grayscale:function(e){return[[.2126+.7874*(1-e),.7152-.7152*(1-e),.0722-.0722*(1-e),0,0],[.2126-.2126*(1-e),.7152+.2848*(1-e),.0722-.0722*(1-e),0,0],[.2126-.2126*(1-e),.7152-.7152*(1-e),.0722+.9278*(1-e),0,0],[0,0,0,1,0],[0,0,0,0,1]]}},D=new Map;function G(e,t,r){var n;D.has(r)?n=D.get(r):(n=new Map,D.set(r,n));var a=Object.entries(e).concat(Object.entries(t).filter(function(e){var t=e[0];return[\"mode\",\"brightness\",\"contrast\",\"grayscale\",\"sepia\"].indexOf(t)>=0})).map(function(e){return e[0]+\":\"+e[1]}).join(\";\");if(n.has(a))return n.get(a);var o=S(r(function(e){var t=e.r,r=e.g,n=e.b,a=e.a,o=void 0===a?1:a,i=t/255,u=r/255,s=n/255,c=Math.max(i,u,s),l=Math.min(i,u,s),d=c-l,f=(c+l)/2;if(0===d)return{h:0,s:0,l:f,a:o};var h=60*(c===i?(u-s)/d%6:c===u?(s-i)/d+2:(i-u)/d+4);return h<0&&(h+=360),{h:h,s:d/(1-Math.abs(2*f-1)),l:f,a:o}}(e))),i=o.r,u=o.g,s=o.b,c=o.a,l=function(e,t){var r=I(t,[[e[0]/255],[e[1]/255],[e[2]/255],[1],[1]]);return[0,1,2].map(function(e){return N(Math.round(255*r[e][0]),0,255)})}([i,u,s],z(t)),d=l[0],f=l[1],h=l[2],p=1===c?function(e){var t=e.r,r=e.g,n=e.b,a=e.a;return\"#\"+(null!=a&&a<1?[t,r,n,Math.round(255*a)]:[t,r,n]).map(function(e){return(e<16?\"0\":\"\")+e.toString(16)}).join(\"\")}({r:d,g:f,b:h}):function(e){var t=e.r,r=e.g,n=e.b,a=e.a;return null!=a&&a<1?\"rgba(\"+E(t)+\", \"+E(r)+\", \"+E(n)+\", \"+E(a,2)+\")\":\"rgb(\"+E(t)+\", \"+E(r)+\", \"+E(n)+\")\"}({r:d,g:f,b:h,a:c});return n.set(a,p),p}function K(e){return e}function _(e){var t=e.h,r=e.s,n=e.l,a=e.a,o=B(n,0,1,0,.9),i=t,u=r;return(n<.2||n>.8||r<.36)&&(u=n<.4?B(n,0,.4,.16,0):B(n,.4,1,0,.16),i=n<.4?205:40),{h:i,s:u,l:o,a:a}}function J(e){var t=e.h,r=e.s,n=e.l,a=e.a,o=B(r,0,1,.25,.4),i=t,u=r;return(n>=.8&&t>200&&t<280||r<.12)&&(u=.05,i=205),{h:i,s:u,l:n<o?n:n<.5?o:B(n,.5,1,o,.1),a:a}}function Q(e,r){return 0===r.mode?G(e,r,_):G(e,t({},r,{mode:0}),J)}function X(e){var t=e.h,r=e.s,n=e.l,a=e.a,o=t>205&&t<=245,i=B(r,0,1,o?B(t,205,245,.7,.7):.7,.6),u=n<.5?B(n,0,.5,.9,i):n<i?i:n,s=t,c=r;return o&&(s=B(s,205,245,205,220)),(n<.2||r<.24)&&(c=.1,s=40),{h:s,s:c,l:u,a:a}}function Y(e,r){return 0===r.mode?G(e,r,_):G(e,t({},r,{mode:0}),X)}function Z(e){var t=e.h,r=e.s,n=e.l,a=e.a,o=B(r,0,1,.2,.3),i=B(r,0,1,.4,.5);return{h:t,s:r,l:B(n,0,1,i,o),a:a}}function ee(e,r){return 0===r.mode?G(e,r,_):G(e,t({},r,{mode:0}),Z)}function te(e,t){return Q(e,t)}function re(e){var t=[];return e.mode===H.dark&&t.push(\"invert(100%) hue-rotate(180deg)\"),100!==e.brightness&&t.push(\"brightness(\"+e.brightness+\"%)\"),100!==e.contrast&&t.push(\"contrast(\"+e.contrast+\"%)\"),0!==e.grayscale&&t.push(\"grayscale(\"+e.grayscale+\"%)\"),0!==e.sepia&&t.push(\"sepia(\"+e.sepia+\"%)\"),0===t.length?null:t.join(\" \")}!function(e){e[e.light=0]=\"light\",e[e.dark=1]=\"dark\"}(H||(H={}));var ne=0,ae=new Map,oe=new Map;function ie(e){return new Promise(function(t,r){var n=++ne;ae.set(n,t),oe.set(n,r),chrome.runtime.sendMessage({type:\"fetch\",data:e,id:n})})}function ue(e){return r(this,void 0,void 0,function(){var t;return n(this,function(r){switch(r.label){case 0:return[4,fetch(e,{cache:\"force-cache\"})];case 1:if((t=r.sent()).ok)return[2,t];throw new Error(\"Unable to load \"+e+\" \"+t.status+\" \"+t.statusText)}})})}function se(e){return r(this,void 0,void 0,function(){var t;return n(this,function(r){switch(r.label){case 0:return[4,ue(e)];case 1:return[4,r.sent().blob()];case 2:return t=r.sent(),[4,new Promise(function(e){var r=new FileReader;r.onloadend=function(){return e(r.result)},r.readAsDataURL(t)})];case 3:return[2,r.sent()]}})})}function ce(e){return r(this,void 0,void 0,function(){var r,a,o;return n(this,function(n){switch(n.label){case 0:return e.startsWith(\"data:\")?(r=e,[3,3]):[3,1];case 1:return[4,le(e)];case 2:r=n.sent(),n.label=3;case 3:return[4,de(r)];case 4:return a=n.sent(),o=function(e){var t=e.naturalWidth*e.naturalHeight,r=Math.min(1,Math.sqrt(1024/t)),n=Math.max(1,Math.round(e.naturalWidth*r)),a=Math.max(1,Math.round(e.naturalHeight*r)),o=document.createElement(\"canvas\");o.width=n,o.height=a;var i=o.getContext(\"2d\");i.imageSmoothingEnabled=!1,i.drawImage(e,0,0,n,a);var u,s,c,l,d,f,h,p,m,g=i.getImageData(0,0,n,a).data,v=0,b=0,y=0;for(c=0;c<a;c++)for(s=0;s<n;s++)l=g[(u=4*(c*n+s))+0]/255,d=g[u+1]/255,f=g[u+2]/255,g[u+3]/255<.05?v++:(p=Math.min(l,d,f),m=Math.max(l,d,f),(h=(m+p)/2)<.4&&b++,h>.7&&y++);var w=n*a,k=w-v;return{isDark:b/k>=.7,isLight:y/k>=.7,isTransparent:v/w>=.1,isLarge:t>=48e4}}(a),[2,t({src:e,dataURL:r,width:a.naturalWidth,height:a.naturalHeight},o)]}})})}function le(e){return r(this,void 0,void 0,function(){return n(this,function(t){switch(t.label){case 0:return function(e){return e.match(/^(.*?\\/{2,3})?(.+?)(\\/|$)/)[2]}(e)!==location.host?[3,2]:[4,se(e)];case 1:return[2,t.sent()];case 2:return[4,ie({url:e,responseType:\"data-url\"})];case 3:return[2,t.sent()]}})})}function de(e){return r(this,void 0,void 0,function(){return n(this,function(t){return[2,new Promise(function(t,r){var n=new Image;n.onload=function(){return t(n)},n.onerror=function(){return r(\"Unable to load image \"+e)},n.src=e})]})})}function fe(e,t){for(var r=e.dataURL,n=e.width,a=e.height,o=['<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"'+n+'\" height=\"'+a+'\">',\"<defs>\",'<filter id=\"darkreader-image-filter\">','<feColorMatrix type=\"matrix\" values=\"'+z(t).slice(0,4).map(function(e){return e.map(function(e){return e.toFixed(3)}).join(\" \")}).join(\" \")+'\" />',\"</filter>\",\"</defs>\",'<image width=\"'+n+'\" height=\"'+a+'\" filter=\"url(#darkreader-image-filter)\" xlink:href=\"'+r+'\" />',\"</svg>\"].join(\"\"),i=new Uint8Array(o.length),u=0;u<o.length;u++)i[u]=o.charCodeAt(u);var s=new Blob([i],{type:\"image/svg+xml\"});return URL.createObjectURL(s)}function he(e,a,o,i){var u=Boolean(o&&o.style&&o.style.getPropertyPriority(e)),d=a;if(e.startsWith(\"--\"))return null;if(e.indexOf(\"color\")>=0&&\"-webkit-print-color-adjust\"!==e||\"fill\"===e||\"stroke\"===e){if(f=function(e,t){if(ge.has(t.toLowerCase()))return t;try{var r=be(t);return e.indexOf(\"background\")>=0?function(e){return Q(r,e)}:e.indexOf(\"border\")>=0||e.indexOf(\"outline\")>=0?function(e){return ee(r,e)}:function(e){return Y(r,e)}}catch(e){return l(\"Color parse error\",e),null}}(e,a))return{property:e,value:f,important:u,sourceValue:d}}else if(\"background-image\"===e){if(f=function(e,a,o,i){var u=this;try{var d=U(we,a),f=U(m,a);if(0===f.length&&0===d.length)return a;var h=function(e){var t=0;return e.map(function(e){var r=a.indexOf(e,t);return t=r+e.length,{match:e,index:r}})},p=h(f).map(function(e){return t({type:\"url\"},e)}).concat(h(d).map(function(e){return t({type:\"gradient\"},e)})).sort(function(e,t){return e.index-t.index}),g=function(e,r){var n,a=e.isDark,o=e.isLight,i=e.isTransparent,u=e.isLarge,s=e.width;if(a&&i&&1===r.mode&&!u&&s>2){c(\"Inverting dark image \"+e.src);var l=fe(e,t({},r,{sepia:N(r.sepia+10,0,100)}));n='url(\"'+l+'\")'}else if(o&&!i&&1===r.mode)if(u)n=\"none\";else{c(\"Dimming light image \"+e.src);var d=fe(e,r);n='url(\"'+d+'\")'}else if(0===r.mode&&o&&!u){c(\"Applying filter to image \"+e.src);var f=fe(e,t({},r,{brightness:N(r.brightness-10,5,200),sepia:N(r.sepia+10,0,100)}));n='url(\"'+f+'\")'}else n=null;return n},y=[],w=0;return p.forEach(function(e,t){var c=e.match,d=e.type,f=e.index,h=w,m=f+c.length;w=m,y.push(function(){return a.substring(h,f)}),y.push(\"url\"===d?function(e){var t=v(e);if(o.parentStyleSheet.href){var a=b(o.parentStyleSheet.href);t=s(a,t)}else t=o.parentStyleSheet.ownerNode&&o.parentStyleSheet.ownerNode.baseURI?s(o.parentStyleSheet.ownerNode.baseURI,t):s(location.origin,t);var c='url(\"'+t+'\")';return function(e){return r(u,void 0,void 0,function(){var r,a;return n(this,function(n){switch(n.label){case 0:return ke.has(t)?(r=ke.get(t),[3,7]):[3,1];case 1:return n.trys.push([1,6,,7]),xe.has(t)?(a=xe.get(t),[4,new Promise(function(e){return a.push(e)})]):[3,3];case 2:return(r=n.sent())?[3,5]:[2,null];case 3:return xe.set(t,[]),[4,ce(t)];case 4:r=n.sent(),ke.set(t,r),xe.get(t).forEach(function(e){return e(r)}),xe.delete(t),n.label=5;case 5:return i()?[2,null]:[3,7];case 6:return l(n.sent()),xe.has(t)&&(xe.get(t).forEach(function(e){return e(null)}),xe.delete(t)),[2,c];case 7:return[2,g(r,e)||c]}})})}}(c):function(e){var t=e.match(/^(.*-gradient)\\((.*)\\)$/),r=t[1],n=t[2],a=/^(from|color-stop|to)\\(([^\\(\\)]*?,\\s*)?(.*?)\\)$/,o=U(/([^\\(\\),]+(\\([^\\(\\)]*(\\([^\\(\\)]*\\)*[^\\(\\)]*)?\\))?[^\\(\\),]*),?/g,n,1).map(function(e){var t=ye(e=e.trim());if(t)return function(e){return te(t,e)};var r=e.lastIndexOf(\" \");if(t=ye(e.substring(0,r)))return function(n){return te(t,n)+\" \"+e.substring(r+1)};var n=e.match(a);return n&&(t=ye(n[3]))?function(e){return n[1]+\"(\"+(n[2]?n[2]+\", \":\"\")+te(t,e)+\")\"}:function(){return e}});return function(e){return r+\"(\"+o.map(function(t){return t(e)}).join(\", \")+\")\"}}(c)),t===p.length-1&&y.push(function(){return a.substring(m)})}),function(e){var t=y.map(function(t){return t(e)});return t.some(function(e){return e instanceof Promise})?Promise.all(t).then(function(e){return e.join(\"\")}):t.join(\"\")}}catch(e){return l(\"Unable to parse gradient \"+a,e),null}}(0,a,o,i))return{property:e,value:f,important:u,sourceValue:d}}else if(e.indexOf(\"shadow\")>=0){var f;if(f=function(e,t){try{var r=0,n=U(/(^|\\s)([a-z]+\\(.+?\\)|#[0-9a-f]+|[a-z]+)(.*?(inset|outset)?($|,))/gi,t,2),a=n.map(function(e,a){var o=r,i=t.indexOf(e,r),u=i+e.length;r=u;var s=ye(e);return s?function(e){return\"\"+t.substring(o,i)+function(e,t){return Q(e,t)}(s,e)+(a===n.length-1?t.substring(u):\"\")}:function(){return t.substring(o,u)}});return function(e){return a.map(function(t){return t(e)}).join(\"\")}}catch(e){return l(\"Unable to parse shadow \"+t,e),null}}(0,a))return{property:e,value:f,important:u,sourceValue:d}}return null}function pe(e,t){var r=[];return t||(r.push(\"html {\"),r.push(\"    background-color: \"+Q({r:255,g:255,b:255},e)+\" !important;\"),r.push(\"}\")),r.push((t?\"\":\"html, body, \")+\"input, textarea, select, button {\"),r.push(\"    background-color: \"+Q({r:255,g:255,b:255},e)+\";\"),r.push(\"}\"),r.push(\"html, body, input, textarea, select, button {\"),r.push(\"    border-color: \"+ee({r:76,g:76,b:76},e)+\";\"),r.push(\"    color: \"+Y({r:0,g:0,b:0},e)+\";\"),r.push(\"}\"),r.push(\"a {\"),r.push(\"    color: \"+Y({r:0,g:64,b:255},e)+\";\"),r.push(\"}\"),r.push(\"table {\"),r.push(\"    border-color: \"+ee({r:128,g:128,b:128},e)+\";\"),r.push(\"}\"),r.push(\"::placeholder {\"),r.push(\"    color: \"+Y({r:169,g:169,b:169},e)+\";\"),r.push(\"}\"),[\"::selection\",\"::-moz-selection\"].forEach(function(t){r.push(t+\" {\"),r.push(\"    background-color: \"+Q({r:0,g:96,b:212},e)+\";\"),r.push(\"    color: \"+Y({r:255,g:255,b:255},e)+\";\"),r.push(\"}\")}),r.push(\"input:-webkit-autofill,\"),r.push(\"textarea:-webkit-autofill,\"),r.push(\"select:-webkit-autofill {\"),r.push(\"    background-color: \"+Q({r:250,g:255,b:189},e)+\" !important;\"),r.push(\"    color: \"+Y({r:0,g:0,b:0},e)+\" !important;\"),r.push(\"}\"),navigator.platform.toLowerCase().startsWith(\"mac\")||(r.push(\"::-webkit-scrollbar {\"),r.push(\"    background-color: \"+Q({r:241,g:241,b:241},e)+\";\"),r.push(\"    color: \"+Y({r:96,g:96,b:96},e)+\";\"),r.push(\"}\"),r.push(\"::-webkit-scrollbar-thumb {\"),r.push(\"    background-color: \"+Q({r:193,g:193,b:193},e)+\";\"),r.push(\"}\"),r.push(\"::-webkit-scrollbar-thumb:hover {\"),r.push(\"    background-color: \"+Q({r:166,g:166,b:166},e)+\";\"),r.push(\"}\"),r.push(\"::-webkit-scrollbar-thumb:active {\"),r.push(\"    background-color: \"+Q({r:96,g:96,b:96},e)+\";\"),r.push(\"}\"),r.push(\"::-webkit-scrollbar-corner {\"),r.push(\"    background-color: \"+Q({r:255,g:255,b:255},e)+\";\"),r.push(\"}\"),r.push(\"* {\"),r.push(\"    scrollbar-color: \"+Q({r:193,g:193,b:193},e)+\" \"+Q({r:241,g:241,b:241},e)+\";\"),r.push(\"}\")),r.join(\"\\n\")}function me(e,t){var r=t.strict,n=[];return n.push(\"html, body, \"+(r?\"body *\":\"body > *\")+\" {\"),n.push(\"    background-color: \"+Q({r:255,g:255,b:255},e)+\" !important;\"),n.push(\"    border-color: \"+ee({r:64,g:64,b:64},e)+\" !important;\"),n.push(\"    color: \"+Y({r:0,g:0,b:0},e)+\" !important;\"),n.push(\"}\"),n.join(\"\\n\")}chrome.runtime.onMessage.addListener(function(e){var t=e.type,r=e.data,n=e.error,a=e.id;if(\"fetch-response\"===t){var o=ae.get(a),i=oe.get(a);ae.delete(a),oe.delete(a),n?i&&i(n):o&&o(r)}});var ge=new Set([\"inherit\",\"transparent\",\"initial\",\"currentcolor\",\"none\"]),ve=new Map;function be(e){if(e=e.trim(),ve.has(e))return ve.get(e);var t=P(e);return ve.set(e,t),t}function ye(e){try{return be(e)}catch(e){return null}}var we=/[\\-a-z]+gradient\\(([^\\(\\)]*(\\(([^\\(\\)]*(\\(.*?\\)))*[^\\(\\)]*\\))){0,15}[^\\(\\)]*\\)/g,ke=new Map,xe=new Map;function Se(){ve.clear(),D.clear(),ke.clear(),xe.clear()}var Ee={\"background-color\":{customProp:\"--darkreader-inline-bgcolor\",cssProp:\"background-color\",dataAttr:\"data-darkreader-inline-bgcolor\",store:new WeakSet},\"background-image\":{customProp:\"--darkreader-inline-bgimage\",cssProp:\"background-image\",dataAttr:\"data-darkreader-inline-bgimage\",store:new WeakSet},\"border-color\":{customProp:\"--darkreader-inline-border\",cssProp:\"border-color\",dataAttr:\"data-darkreader-inline-border\",store:new WeakSet},\"border-bottom-color\":{customProp:\"--darkreader-inline-border-bottom\",cssProp:\"border-bottom-color\",dataAttr:\"data-darkreader-inline-border-bottom\",store:new WeakSet},\"border-left-color\":{customProp:\"--darkreader-inline-border-left\",cssProp:\"border-left-color\",dataAttr:\"data-darkreader-inline-border-left\",store:new WeakSet},\"border-right-color\":{customProp:\"--darkreader-inline-border-right\",cssProp:\"border-right-color\",dataAttr:\"data-darkreader-inline-border-right\",store:new WeakSet},\"border-top-color\":{customProp:\"--darkreader-inline-border-top\",cssProp:\"border-top-color\",dataAttr:\"data-darkreader-inline-border-top\",store:new WeakSet},\"box-shadow\":{customProp:\"--darkreader-inline-boxshadow\",cssProp:\"box-shadow\",dataAttr:\"data-darkreader-inline-boxshadow\",store:new WeakSet},color:{customProp:\"--darkreader-inline-color\",cssProp:\"color\",dataAttr:\"data-darkreader-inline-color\",store:new WeakSet},fill:{customProp:\"--darkreader-inline-fill\",cssProp:\"fill\",dataAttr:\"data-darkreader-inline-fill\",store:new WeakSet},stroke:{customProp:\"--darkreader-inline-stroke\",cssProp:\"stroke\",dataAttr:\"data-darkreader-inline-stroke\",store:new WeakSet},\"outline-color\":{customProp:\"--darkreader-inline-outline\",cssProp:\"outline-color\",dataAttr:\"data-darkreader-inline-outline\",store:new WeakSet}},Ae=Object.values(Ee),Me=[\"style\",\"fill\",\"stroke\",\"bgcolor\",\"color\"],Le=Me.map(function(e){return\"[\"+e+\"]\"}).join(\", \");var Pe=null;function Re(e){Pe&&Pe.disconnect(),(Pe=new MutationObserver(function(t){t.forEach(function(t){var r,n,a,o=(r=Array.from(t.addedNodes),n=Le,a=[],r.forEach(function(e){e instanceof Element&&(e.matches(n)&&a.push(e),a.push.apply(a,Array.from(e.querySelectorAll(n))))}),a);o.length>0&&o.forEach(function(t){return e(t)}),\"attributes\"===t.type&&(Me.includes(t.attributeName)&&e(t.target),Ae.filter(function(e){var r=e.store,n=e.dataAttr;return r.has(t.target)&&!t.target.hasAttribute(n)}).forEach(function(e){var r=e.dataAttr;return t.target.setAttribute(r,\"\")}))})})).observe(document,{childList:!0,subtree:!0,attributes:!0,attributeFilter:Me.concat(Ae.map(function(e){return e.dataAttr}))})}var Ce=new WeakMap,Te=[\"brightness\",\"contrast\",\"grayscale\",\"sepia\",\"mode\"];function je(e,t){return Me.map(function(t){return t+'=\"'+e.getAttribute(t)+'\"'}).concat(Te.map(function(e){return e+'=\"'+t[e]+'\"'})).join(\" \")}function Oe(e,t){if(je(e,t)!==Ce.get(e)){var r=new Set(Object.keys(Ee));if(e.hasAttribute(\"bgcolor\"))((n=e.getAttribute(\"bgcolor\")).match(/^[0-9a-f]{3}$/i)||n.match(/^[0-9a-f]{6}$/i))&&(n=\"#\"+n),s(\"background-color\",\"background-color\",n);if(e.hasAttribute(\"color\"))((n=e.getAttribute(\"color\")).match(/^[0-9a-f]{3}$/i)||n.match(/^[0-9a-f]{6}$/i))&&(n=\"#\"+n),s(\"color\",\"color\",n);if(e.hasAttribute(\"fill\")&&e instanceof SVGElement){var n=e.getAttribute(\"fill\"),a=!1;if(!(e instanceof SVGTextElement)){var o=e.getBoundingClientRect(),i=o.width,u=o.height;a=i>32||u>32}s(\"fill\",a?\"background-color\":\"color\",n)}if(e.hasAttribute(\"stroke\")){n=e.getAttribute(\"stroke\");s(\"stroke\",e instanceof SVGLineElement||e instanceof SVGTextElement?\"border-color\":\"color\",n)}e.style&&f(e.style,function(e,t){\"background-image\"===e&&t.indexOf(\"url\")>=0||Ee.hasOwnProperty(e)&&s(e,e,t)}),e.style&&e instanceof SVGTextElement&&e.style.fill&&s(\"fill\",\"color\",e.style.getPropertyValue(\"fill\")),Array.from(r).forEach(function(t){var r=Ee[t],n=r.store,a=r.dataAttr;n.delete(e),e.removeAttribute(a)}),Ce.set(e,je(e,t))}function s(n,a,o){var i=Ee[n],u=i.customProp,s=i.dataAttr,c=he(a,o,null,null);if(c){var l=c.value;\"function\"==typeof l&&(l=l(t)),e.style.setProperty(u,l),e.hasAttribute(s)||e.setAttribute(s,\"\"),r.delete(n)}}}var qe=\"theme-color\",We='meta[name=\"'+qe+'\"]',Fe=null,$e=null;function Be(e,t){Fe=Fe||e.content;try{var r=P(Fe);e.content=Q(r,t)}catch(e){l(e)}}function Ne(e){var t,r=!1,n=null;return Object.assign(function(){for(var a=[],o=0;o<arguments.length;o++)a[o]=arguments[o];t=a,n?r=!0:(e.apply(void 0,t),n=requestAnimationFrame(function(){n=null,r&&(e.apply(void 0,t),r=!1)}))},{cancel:function(){cancelAnimationFrame(n),r=!1,n=null}})}function Ie(e){e&&e.parentNode&&e.parentNode.removeChild(e)}function Ue(e,t){var r,n,a=(n=0,(r={seconds:10}).seconds&&(n+=1e3*r.seconds),r.minutes&&(n+=60*r.minutes*1e3),r.hours&&(n+=60*r.hours*60*1e3),r.days&&(n+=24*r.days*60*60*1e3),n),o=e.previousSibling,i=e.parentElement;if(!i)return l(\"Unable to watch for node position: parent element not found\",e,o),{stop:function(){}};var u=0,s=null,c=Ne(function(){u++;var r=Date.now();if(null==s)s=r;else if(u>=10){if(r-s<a)return l(\"Node position watcher stopped: some script conflicts with Dark Reader and can cause high CPU usage\",e,o),void h();s=r,u=1}if(o&&o.parentElement!==i)return l(\"Unable to restore node position: sibling was removed\",e,o,i),void h();l(\"Node was removed, restoring it's position\",e,o,i),i.insertBefore(e,o?o.nextSibling:i.firstChild),t&&t()}),d=new MutationObserver(function(){e.parentElement||c()}),f=function(){d.observe(i,{childList:!0})},h=function(){d.disconnect()};return f(),{run:f,stop:h}}var ze=function(){try{return document.querySelector(\"x /deep/ x\"),!0}catch(e){return!1}}()?'html /deep/ link[rel*=\"stylesheet\" i], html /deep/ style':'html link[rel*=\"stylesheet\" i], html style';function He(e){return(e instanceof HTMLStyleElement||e instanceof HTMLLinkElement&&e.rel&&e.rel.toLowerCase().includes(\"stylesheet\"))&&!e.classList.contains(\"darkreader\")&&\"print\"!==e.media}var Ve=function(){var e=[],t=null;function r(){for(var r;r=e.shift();)r();t=null}return{add:function(n){e.push(n),t||(t=requestAnimationFrame(r))},cancel:function(){e.splice(0),cancelAnimationFrame(t),t=null}}}();function De(e,t){for(var a=t.update,o=t.loadingStart,i=t.loadingEnd,u=[],s=e;(s=s.nextElementSibling)&&s.matches(\".darkreader\");)u.push(s);var c=u.find(function(e){return e.matches(\".darkreader--cors\")})||null,p=u.find(function(e){return e.matches(\".darkreader--sync\")})||null,m=null,v=null,y=!1;function w(){return y}var k=new MutationObserver(function(){a()}),S={attributes:!0,childList:!0,characterData:!0};function E(){return e instanceof HTMLStyleElement&&e.textContent.trim().match(g)}function A(){if(c)return c.sheet.cssRules;if(null==e.sheet)return null;if(e instanceof HTMLLinkElement)try{return e.sheet.cssRules}catch(e){return l(e),null}return E()?null:W()}var M=!1,L=!1;function P(){return r(this,void 0,void 0,function(){var t,r,a;return n(this,function(n){switch(n.label){case 0:if(!(e instanceof HTMLLinkElement))return[3,6];if(null!=e.sheet)return[3,4];n.label=1;case 1:return n.trys.push([1,3,,4]),[4,(o=e,new Promise(function(e,t){var r=function(){o.removeEventListener(\"load\",n),o.removeEventListener(\"error\",a)},n=function(){r(),e()},a=function(){r(),t(\"Link loading failed \"+o.href)};o.addEventListener(\"load\",n),o.addEventListener(\"error\",a)}))];case 2:return n.sent(),y?[2,null]:[3,4];case 3:return l(n.sent()),L=!0,[2,null];case 4:try{if(null!=e.sheet.cssRules)return[2,e.sheet.cssRules]}catch(e){l(e)}return[4,Ge(e.href)];case 5:return t=n.sent(),r=b(e.href),y?[2,null]:[3,7];case 6:if(!E())return[2,null];t=e.textContent.trim(),r=b(location.href),n.label=7;case 7:if(!t)return[3,12];n.label=8;case 8:return n.trys.push([8,10,,11]),[4,Ke(t,r)];case 9:return a=n.sent(),(c=function(e,t){if(!t)return null;var r=document.createElement(\"style\");return r.classList.add(\"darkreader\"),r.classList.add(\"darkreader--cors\"),r.media=\"screen\",r.textContent=t,e.parentNode.insertBefore(r,e.nextSibling),r.sheet.disabled=!0,r}(e,a))&&(m=Ue(c)),[3,11];case 10:return l(n.sent()),[3,11];case 11:if(c)return[2,c.sheet.cssRules];n.label=12;case 12:return[2,null]}var o})})}var R=0,C=new Map,T=new Map,j=null;var O=null,q=null;function W(){try{return e.sheet.cssRules}catch(e){return l(e),null}}function F(){cancelAnimationFrame(q)}function $(){k.disconnect(),m&&m.stop(),v&&v.stop(),y=!0,F()}return{details:function(){var e=A();return e?{variables:function(e){var t=new Map;return e&&d(e,function(e){e.style&&f(e.style,function(e,r){h(e)&&t.set(e,r)})}),t}(e)}:M||L?null:(M=!0,o(),P().then(function(e){M=!1,i(),e&&a()}).catch(function(e){l(e),M=!1,i()}),null)},render:function(t,r){var n=A();if(n){y=!1;var a=0===T.size,o=new Set(T.keys()),i=function(e){return[\"mode\",\"brightness\",\"contrast\",\"grayscale\",\"sepia\"].map(function(t){return t+\":\"+e[t]}).join(\";\")}(t),u=i!==j,s=[];if(d(n,function(t){var n=t.cssText,i=!1;o.delete(n),C.has(n)||(C.set(n,n),i=!0);var u=null,c=null;if(r.size>0||n.includes(\"var(\")){var l=x(n,r);C.get(n)!==l&&(C.set(n,l),i=!0,(u=document.createElement(\"style\")).classList.add(\"darkreader\"),u.classList.add(\"darkreader--vars\"),u.media=\"screen\",u.textContent=l,e.parentNode.insertBefore(u,e.nextSibling),c=u.sheet.cssRules[0])}if(i){a=!0;var d=[],h=c||t;h&&h.style&&f(h.style,function(e,r){var n=he(e,r,t,w);n&&d.push(n)});var p=null;d.length>0&&(p={selector:t.selectorText,declarations:d},t.parentRule instanceof CSSMediaRule&&(p.media=t.parentRule.media.mediaText),s.push(p)),T.set(n,p),Ie(u)}else s.push(T.get(n))}),o.forEach(function(e){C.delete(e),T.delete(e)}),j=i,a||u){R++;var l=[],h=new Map,m=0;s.filter(function(e){return e}).forEach(function(e){var r=e.selector,n=e.declarations,a=e.media;n.forEach(function(e){var n=e.property,o=e.value,i=e.important,u=e.sourceValue;if(\"function\"==typeof o){var s=o(t);if(s instanceof Promise){var c=l.length,d=m++;l.push({media:a,selector:r,property:n,value:null,important:i,asyncKey:d,sourceValue:u});var f=R;s.then(function(e){e&&!y&&f===R&&(l[c].value=e,Ve.add(function(){var e,t,r,n,a;y||f!==R||(e=d,t=h.get(e),r=t.declarations,n=t.target,a=t.index,n.deleteRule(a),g(n,a,r),h.delete(e))}))})}else l.push({media:a,selector:r,property:n,value:s,important:i,sourceValue:u})}else l.push({media:a,selector:r,property:n,value:o,important:i,sourceValue:u})})}),function t(){var r=[];l.forEach(function(e,t){var n,a,o=0===t?null:l[t-1],i=o&&o.media===e.media,u=o&&i&&o.selector===e.selector;i?n=r[r.length-1]:(n=[],r.push(n)),u?a=n[n.length-1]:(a=[],n.push(a)),a.push(e)}),p||((p=document.createElement(\"style\")).classList.add(\"darkreader\"),p.classList.add(\"darkreader--sync\"),p.media=\"screen\"),v&&v.stop(),e.parentNode.insertBefore(p,c?c.nextSibling:e.nextSibling);for(var n=p.sheet,a=n.cssRules.length-1;a>=0;a--)n.deleteRule(a);r.forEach(function(e){var t,r=e[0][0].media;r?(n.insertRule(\"@media \"+r+\" {}\",n.cssRules.length),t=n.cssRules[n.cssRules.length-1]):t=n,e.forEach(function(e){var r=e.filter(function(e){return null==e.value});r.length>0&&r.forEach(function(r){var n=r.asyncKey;return h.set(n,{declarations:e,target:t,index:t.cssRules.length})}),g(t,t.cssRules.length,e)})}),v?v.run():v=Ue(p,t)}()}}function g(e,t,r){var n=r[0].selector;e.insertRule(n+\" {}\",t);var a=e.cssRules.item(t).style;r.forEach(function(e){var t=e.property,r=e.value,n=e.important,o=e.sourceValue;a.setProperty(t,null==r?o:r,n?\"important\":\"\")})}},pause:$,destroy:function(){$(),Ie(c),Ie(p)},watch:function(){k.observe(e,S),e instanceof HTMLStyleElement&&function(){e.sheet&&W()&&(O=e.sheet.cssRules.length),F();var t=function(){e.sheet&&W()&&e.sheet.cssRules.length!==O&&(O=e.sheet.cssRules.length,a()),q=requestAnimationFrame(t)};t()}()}}}function Ge(e){return r(this,void 0,void 0,function(){return n(this,function(t){switch(t.label){case 0:return e.startsWith(\"data:\")?[4,fetch(e)]:[3,3];case 1:return[4,t.sent().text()];case 2:return[2,t.sent()];case 3:return[4,ie({url:e,responseType:\"text\"})];case 4:return[2,t.sent()]}})})}function Ke(e,t){return r(this,void 0,void 0,function(){var r,a,o,i,u,c,d;return n(this,function(n){switch(n.label){case 0:e=function(e,t){return e.replace(m,function(e){var r=v(e);return'url(\"'+s(t,r)+'\")'})}(e=function(e){return e.replace(w,\"\")}(e=e.replace(y,\"\")),t),r=U(g,e),a=0,o=r,n.label=1;case 1:if(!(a<o.length))return[3,8];i=o[a],u=v(i.substring(8).replace(/;$/,\"\")),c=s(t,u),d=void 0,n.label=2;case 2:return n.trys.push([2,5,,6]),[4,Ge(c)];case 3:return[4,Ke(d=n.sent(),b(c))];case 4:return d=n.sent(),[3,6];case 5:return l(n.sent()),d=\"\",[3,6];case 6:e=e.split(i).join(d),n.label=7;case 7:return a++,[3,1];case 8:return[2,e=e.trim()]}})})}var _e=null;function Je(e){var t=[];return Array.from(e).forEach(function(e){e instanceof Element&&(He(e)&&t.push(e),t.push.apply(t,Array.from(e.querySelectorAll(ze)).filter(He)))}),t}function Qe(e,t){Array.from(e).forEach(function(e){e instanceof Element&&(e.shadowRoot&&t(e),Qe(e.childNodes,t))})}var Xe=new Set;var Ye=new Map,Ze=new Map,et=null,tt=null,rt=null;function nt(e){var t=(document.head||document).querySelector(\".\"+e);return t||((t=document.createElement(\"style\")).classList.add(\"darkreader\"),t.classList.add(e),t.media=\"screen\"),t}var at=new Map;function ot(e,t){at.has(t)&&at.get(t).stop(),at.set(t,Ue(e))}function it(){var e=nt(\"darkreader--fallback\");document.head.insertBefore(e,document.head.firstChild),e.textContent=me(et,{strict:!0}),ot(e,\"fallback\");var r=nt(\"darkreader--user-agent\");document.head.insertBefore(r,e.nextSibling),r.textContent=pe(et,rt),ot(r,\"user-agent\");var n,a,o=nt(\"darkreader--text\");document.head.insertBefore(o,e.nextSibling),et.useFont||et.textStroke>0?o.textContent=(n=et,(a=[]).push(\"* {\"),n.useFont&&n.fontFamily&&a.push(\"  font-family: \"+n.fontFamily+\" !important;\"),n.textStroke>0&&(a.push(\"  -webkit-text-stroke: \"+n.textStroke+\"px !important;\"),a.push(\"  text-stroke: \"+n.textStroke+\"px !important;\")),a.push(\"}\"),a.join(\"\\n\")):o.textContent=\"\",ot(o,\"text\");var i=nt(\"darkreader--invert\");document.head.insertBefore(i,o.nextSibling),tt&&Array.isArray(tt.invert)&&tt.invert.length>0?i.textContent=[tt.invert.join(\", \")+\" {\",\"    filter: \"+re(t({},et,{contrast:0===et.mode?et.contrast:N(et.contrast-10,0,100)}))+\" !important;\",\"}\"].join(\"\\n\"):i.textContent=\"\",ot(i,\"invert\");var u=nt(\"darkreader--inline\");document.head.insertBefore(u,i.nextSibling),u.textContent=Ae.map(function(e){var t=e.dataAttr,r=e.customProp;return[\"[\"+t+\"] {\",\"  \"+e.cssProp+\": var(\"+r+\") !important;\",\"}\"].join(\"\\n\")}).join(\"\\n\"),ot(u,\"inline\");var s=nt(\"darkreader--override\");document.head.appendChild(s),s.textContent=tt&&tt.css?tt.css.replace(/\\${(.+?)}/g,function(e,t){try{var r=be(t);return G(r,et,K)}catch(e){return l(e),t}}):\"\",ot(s,\"override\")}function ut(){var e=document.head.querySelector(\".darkreader--fallback\");e&&(e.textContent=\"\")}var st=0,ct=new Set;function lt(e){if(!Ye.has(e)){var t=++st,r=De(e,{update:function(){var e=r.details();e&&(0===e.variables.size?r.render(et,Ze):(dt(e.variables),ht()))},loadingStart:function(){if(!mt()||!bt){ct.add(t);var e=document.querySelector(\".darkreader--fallback\");e.textContent||(e.textContent=me(et,{strict:!1}))}},loadingEnd:function(){ct.delete(t),0===ct.size&&mt()&&ut()}});return Ye.set(e,r),r}}function dt(e){0!==e.size&&(e.forEach(function(e,t){return Ze.set(t,e)}),Ze.forEach(function(e,t){return Ze.set(t,x(e,Ze))}))}function ft(e){var t=Ye.get(e);t&&(t.destroy(),Ye.delete(e))}var ht=Ne(function(e){Ye.forEach(function(e){return e.render(et,Ze)}),e&&e()}),pt=function(){ht.cancel()};function mt(){return\"complete\"===document.readyState||\"interactive\"===document.readyState}function gt(){mt()&&(document.removeEventListener(\"readystatechange\",gt),0===ct.size&&ut())}var vt=null,bt=!document.hidden;function yt(){document.removeEventListener(\"visibilitychange\",vt),vt=null}function wt(){function e(){!function(){pt(),dt(p(document.documentElement));var e=Array.from(document.querySelectorAll(ze)).filter(function(e){return!Ye.has(e)&&He(e)}).map(function(e){return lt(e)}),t=e.map(function(e){return e.details()}).filter(function(e){return e&&e.variables.size>0}).map(function(e){return e.variables});0===t.length?(Ye.forEach(function(e){return e.render(et,Ze)}),0===ct.size&&ut()):(t.forEach(function(e){return dt(e)}),ht(function(){0===ct.size&&ut()})),e.forEach(function(e){return e.watch()}),Array.from(document.querySelectorAll(Le)).forEach(function(e){return Oe(e,et)})}(),function(e){function t(t){var n=t.reduce(function(e,t){return e.concat(Je(t.addedNodes))},[]),a=t.reduce(function(e,t){return e.concat(Je(t.removedNodes))},[]),o=t.filter(function(e){var t=e.target;return\"attributes\"===e.type&&He(t)}).reduce(function(e,t){var r=t.target;return e.push(r),e},[]);n.length+a.length+o.length>0&&e({created:n,updated:o,removed:a});var i=[];t.forEach(function(e){e.addedNodes.forEach(function(e){i.push(e)})}),Qe(i,r)}function r(e){var r=new MutationObserver(t);r.observe(e.shadowRoot,n),Xe.add(r)}_e&&(_e.disconnect(),Xe.forEach(function(e){return e.disconnect()}),Xe.clear());var n={childList:!0,subtree:!0,attributes:!0,attributeFilter:[\"rel\"]};(_e=new MutationObserver(t)).observe(document.documentElement,n),Qe(document.documentElement.children,r)}(function(e){var t=e.created,r=e.updated,n=e.removed,a=new Set(t),o=new Set(n.filter(function(e){return a.has(e)}));n.filter(function(e){return!o.has(e)}).forEach(function(e){return ft(e)});var i=Array.from(new Set(t.concat(r))).filter(function(e){return!Ye.has(e)}).map(function(e){return lt(e)}),u=i.map(function(e){return e.details()}).filter(function(e){return e&&e.variables.size>0}).map(function(e){var t=e.variables;return t});0===u.length?i.forEach(function(e){return e.render(et,Ze)}):(u.forEach(function(e){return dt(e)}),ht()),i.forEach(function(e){return e.watch()})}),Re(function(e){if(Oe(e,et),e===document.documentElement){var t=p(document.documentElement);t.size>0&&(dt(t),ht())}}),document.addEventListener(\"readystatechange\",gt)}var t,r,n,a;it(),document.hidden?(t=e,r=Boolean(vt),vt=function(){document.hidden||(yt(),t(),bt=!0)},r||document.addEventListener(\"visibilitychange\",vt)):e(),n=et,(a=document.querySelector(We))?Be(a,n):($e&&$e.disconnect(),($e=new MutationObserver(function(e){e:for(var t=0,r=e;t<r.length;t++)for(var a=r[t],o=0,i=Array.from(a.addedNodes);o<i.length;o++){var u=i[o];if(u instanceof HTMLMetaElement&&u.name===qe){$e.disconnect(),$e=null,Be(u,n);break e}}})).observe(document.head,{childList:!0}))}function kt(){Ye.forEach(function(e){return e.pause()}),Array.from(at.values()).forEach(function(e){return e.stop()}),at.clear(),_e&&(_e.disconnect(),_e=null,Xe.forEach(function(e){return e.disconnect()}),Xe.clear()),Pe&&(Pe.disconnect(),Pe=null),document.removeEventListener(\"readystatechange\",gt)}function xt(e,t,r){if(et=e,tt=t,rt=r,document.head)wt();else{if(!navigator.userAgent.includes(\"Firefox\")){var n=nt(\"darkreader--fallback\");document.documentElement.appendChild(n),n.textContent=me(et,{strict:!0})}var a=new MutationObserver(function(){document.head&&(a.disconnect(),wt())});a.observe(document,{childList:!0,subtree:!0})}}function St(){yt(),pt(),kt(),Se(),Ie(document.querySelector(\".darkreader--fallback\")),document.head&&(!function(){$e&&($e.disconnect(),$e=null);var e=document.querySelector(We);e&&Fe&&(e.content=Fe)}(),Ie(document.head.querySelector(\".darkreader--user-agent\")),Ie(document.head.querySelector(\".darkreader--text\")),Ie(document.head.querySelector(\".darkreader--invert\")),Ie(document.head.querySelector(\".darkreader--inline\")),Ie(document.head.querySelector(\".darkreader--override\"))),Array.from(Ye.keys()).forEach(function(e){return ft(e)}),Array.from(document.querySelectorAll(\".darkreader\")).forEach(Ie)}var Et={mode:1,brightness:100,contrast:100,grayscale:0,sepia:0,useFont:!1,fontFamily:\"\",textStroke:0,engine:i.dynamicTheme,stylesheet:\"\"};e.disable=function(){St()},e.enable=function(e,r,n){void 0===r&&(r=null),void 0===n&&(n=!1);var a=t({},Et,e);if(a.engine!==i.dynamicTheme)throw new Error(\"Theme engine is not supported\");xt(a,r,n)},Object.defineProperty(e,\"__esModule\",{value:!0})});\n" +
                        "//# sourceMappingURL=/sm/5085a493fdf8b25bf571e110a126160385e8b45208f0bddc6a49f6f3565bc9b2.map");
            }
            public void darkmode() {
                webView.loadUrl("javascript:DarkReader.enable({\n" +
                        "    brightness: 100,\n" +
                        "    contrast: 90,\n" +
                        "    sepia: 10\n" +
                        "});");
                webView.setBackgroundColor(Color.parseColor("#202020"));
            }
        });

        // 파일 다운로드 처리
        webView.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("파일 다운로드 중...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "파일 다운로드 시작", Toast.LENGTH_LONG).show();
            }});
        web_load();
    }

    public void mOnClick(View button){
        PopupMenu popupMenu = new PopupMenu(this, button);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.forward:
                        webView.goForward();
                        break;
                    case R.id.reload:
                        webView.reload();
                        break;
                    case R.id.thisblank:
                        addindextab_list(tab, Url);
                        tab++;
                        break;
                    case R.id.website:
                        webView.loadUrl("http://zzz2757.kro.kr/Secretbrowser/Download.html");
                        break;
                    case R.id.clear:
                        webView.clearHistory();
                        webView.clearFormData();
                        webView.clearCache(true);
                        android.widget.Toast.makeText(getApplicationContext(), "방문기록및 캐시,쿠키 삭제 완료", android.widget.Toast.LENGTH_LONG).show();
                        break;
                    case R.id.setting:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.verinfo:
                        if (internet_connection == 0) {
                            new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                    .setTitle("Secret browser 정보")
                                    .setMessage("애플리케이션 버전\nSecret borwser " + VERSION_NAME + "  " + veryear + "년 " + vermonth + "월 " + verday + "일 릴리즈" + "\n\n최신 애플리케이션 버전\n업데이트 확인에 실패 하였습니다 인터넷 연결을 확인하세요." + "\n\n프로그래밍,앱디자인\n이현준")
                                    .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int sumthin) {

                                        }
                                    })
                                    .show();
                        } else if (internet_connection == 1 ||internet_connection == 2){
                            if (server_connection == 0) {
                                new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                        .setTitle("Secret browser 정보")
                                        .setMessage("애플리케이션 버전\nSecret borwser " + VERSION_NAME + "  " + veryear + "년 " + vermonth + "월 " + verday + "일 릴리즈" + "\n\n최신 애플리케이션 버전\n업데이트 확인 서버 연결에 실패하였습니다\n\n프로그래밍,앱디자인\n이현준")
                                        .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dlg, int sumthin) {

                                            }
                                        })
                                        .show();
                            } else if (server_connection == 1){
                                new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                        .setTitle("Secret browser 정보")
                                        .setMessage("애플리케이션 버전\nSecret borwser " + VERSION_NAME + "  " + veryear + "년 " + vermonth + "월 " + verday + "일 릴리즈" + "\n\n최신 애플리케이션 버전\n" + newver + "." + newvercode + "  " + newyear + "년" + newmonth + "월" + newday + "일 릴리즈\n\n프로그래밍,앱디자인\n이현준")
                                        .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dlg, int sumthin) {

                                            }
                                        })
                                        .show();
                            } else {
                                new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                        .setTitle("Secret browser 정보")
                                        .setMessage("애플리케이션 버전\nSecret borwser " + VERSION_NAME + "  " + veryear + "년 " + vermonth + "월 " + verday + "일 릴리즈" + "\n\n최신 애플리케이션 버전\n서버 연결을 확인 하는 중에 알 수 없는 에러가 발생하였습니다 server_connection : " + server_connection + "\n\n프로그래밍,앱디자인\n이현준")
                                        .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dlg, int sumthin) {

                                            }
                                        })
                                        .show();
                            }
                        } else {
                            new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                                    .setTitle("Secret browser 정보")
                                    .setMessage("애플리케이션 버전\nSecret borwser " + VERSION_NAME + "  " + veryear + "년 " + vermonth + "월 " + verday + "일 릴리즈" + "\n\n최신 애플리케이션 버전\n인터넷 연결을 확인 하는 중에 알 수 없는 에러가 발생하였습니다 internet_connection : " + internet_connection + "\n\n프로그래밍,앱디자인\n이현준")
                                    .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int sumthin) {

                                        }
                                    })
                                    .show();
                        }
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
