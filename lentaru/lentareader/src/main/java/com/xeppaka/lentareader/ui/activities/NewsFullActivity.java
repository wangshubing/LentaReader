package com.xeppaka.lentareader.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.ui.fragments.NewsFullFragment;
import com.xeppaka.lentareader.utils.LentaDebugUtils;

public class NewsFullActivity extends ActionBarActivity {
	// private NewsObjectFullFragment<?> fullFragment;
    private NewsFullFragment fullFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        LentaDebugUtils.strictMode();

        setTitle(null);
        //getActionBar().setIcon(R.drawable.lenta_icon);
        getSupportActionBar().setLogo(R.drawable.ab_lenta_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.full_news_activity);

		final long newsId = getIntent().getLongExtra("newsId", Dao.NO_ID);
		
		if (newsId != Dao.NO_ID) {
            fullFragment = new NewsFullFragment(newsId);
			getSupportFragmentManager().beginTransaction().replace(R.id.full_news_fragment_container, fullFragment).commit();
		} else {
//            final long articleId = getIntent().getLongExtra("articleId", Dao.NO_ID);
//
//            if (articleId != Dao.NO_ID) {
//                fullFragment = new ArticleFullFragment(articleId);
//                getSupportFragmentManager().beginTransaction().replace(R.id.full_news_fragment_container, fullFragment).commit();
//            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_full_menu_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String link;

        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.action_copy_link:
                link = fullFragment.getLink();

                if (link != null) {
                    final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    final ClipData clip = ClipData.newPlainText("URL", link);
                    clipboardManager.setPrimaryClip(clip);

                    Toast.makeText(this, R.string.info_link_copied_toast, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_link_copied_toast, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.action_open_in_browser:
                link = fullFragment.getLink();

                if (link != null) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.error_link_open_in_browser_toast, Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
