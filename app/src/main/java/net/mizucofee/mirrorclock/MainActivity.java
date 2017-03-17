package net.mizucofee.mirrorclock;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> news = new ArrayList<>();

    @BindView(R.id.week) MirroredTextView weekTv;
    @BindView(R.id.date) MirroredTextView dateTv;
    @BindView(R.id.hour) MirroredTextView hourTv;
    @BindView(R.id.min) MirroredTextView minTv;
    @BindView(R.id.sec) MirroredTextView secTv;
    @BindView(R.id.coron) MirroredTextView coronTv;
    @BindView(R.id.marquee) MirroredTextView marqueeTv;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        ButterKnife.bind(this);

        getNews();
        update();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!flag){}
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        animateText();
                    }
                });
            }
        }).start();
    }

    int now = 0;

    private void animateText(){
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF,	+1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );

        animation.setInterpolator(new LinearInterpolator());

        animation.setDuration(news.get(now).length() * 500);

        marqueeTv.setText(news.get(now));
        marqueeTv.setLayoutParams(new LinearLayout.LayoutParams((int)(getResources().getDisplayMetrics().density * 54 * news.get(now).length()), LinearLayout.LayoutParams.WRAP_CONTENT));

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                now++;
                if(news.size() <= now) {
                    now = 0;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!flag){}
                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    animateText();
                                }
                            });
                        }
                    }).start();
                } else
                    animateText();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        marqueeTv.startAnimation(animation);
    }

    private void update(){
        final Handler h = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Date date = new Date(System.currentTimeMillis());
                            dateTv.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(date));
                            hourTv.setText(new SimpleDateFormat("HH", Locale.US).format(date));
                            minTv.setText(new SimpleDateFormat("mm", Locale.US).format(date));
                            secTv.setText(new SimpleDateFormat("ss", Locale.US).format(date));
                            coronTv.setText((Integer.parseInt(new SimpleDateFormat("ss", Locale.US).format(date)) & 1) == 0 ? ":" : " ");
                            weekTv.setText(new SimpleDateFormat("E", Locale.US).format(date));
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private boolean flag = false;
    private void getNews(){
        flag = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String get = null;
                try {
                    URLConnection connection = new URL("https://headlines.yahoo.co.jp/rss/all-dom.xml").openConnection();
                    DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = dbfactory.newDocumentBuilder(); // DocumentBuilderインスタンス

                    Document doc = builder.parse(connection.getInputStream());

                    NodeList localNodeList =
                            ((Element) doc.getElementsByTagName("channel").item(0)).getElementsByTagName("item");

                    news.clear();
                    for (int i = 0;localNodeList.getLength() != i;i++){
                        Element elementItem = (Element) localNodeList.item(i);
                        Element elementItemName = (Element) elementItem.getElementsByTagName("title").item(0);
                        news.add( elementItemName.getFirstChild().getNodeValue());
                    }
                    flag = true;
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
