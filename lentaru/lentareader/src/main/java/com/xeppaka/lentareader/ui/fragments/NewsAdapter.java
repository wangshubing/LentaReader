package com.xeppaka.lentareader.ui.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsAdapter extends NewsObjectAdapter<News> {

    private final ImageDao imageDao;

	private static class ViewHolder {
		private final TextView newsTitle;
        private final TextView newsDescription;
        private final TextView newsDate;
        private final TextView newsImageCaption;
        private final TextView newsImageCredits;
        private final View newsDescriptionPanel;
        private final ImageView newsImage;
		private BitmapReference imageRef;
        private int position;
        private String imageUrl;
        private AsyncTask asyncTask;

        public ViewHolder(ImageView newsImage, TextView newsTitle, TextView newsDescription, TextView newsDate, TextView newsImageCaption, TextView newsImageCredits, View newsDescriptionPanel, int position) {
			this.newsImage = newsImage;
			this.newsTitle = newsTitle;
            this.newsDescription = newsDescription;
            this.newsDate = newsDate;
            this.newsImageCaption = newsImageCaption;
            this.newsImageCredits = newsImageCredits;
            this.newsDescriptionPanel = newsDescriptionPanel;
            this.position = position;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}

        public TextView getNewsDescription() {
            return newsDescription;
        }

        public TextView getNewsDate() {
            return newsDate;
        }

        public TextView getNewsImageCaption() {
            return newsImageCaption;
        }

        public TextView getNewsImageCredits() {
            return newsImageCredits;
        }

        public ImageView getNewsImage() {
			return newsImage;
		}

        public View getNewsDescriptionPanel() {
            return newsDescriptionPanel;
        }

        public BitmapReference getImage() {
			return imageRef;
		}
		
		public void setImage(BitmapReference imageRef) {
			this.imageRef = imageRef;
		}

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public AsyncTask getAsyncTask() {
            return asyncTask;
        }

        public void setAsyncTask(AsyncTask asyncTask) {
            this.asyncTask = asyncTask;
        }
    }

	public NewsAdapter(Context context) {
		super(context);

        imageDao = ImageDao.newInstance();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		ImageView newsImageView;
		TextView newsTitleTextView;
        TextView newsDescriptionTextView;
        TextView newsDateTextView;
        TextView newsCaptionTextView;
        TextView newsCreditsTextView;
        View newsDescriptionPanel;

		News news = getItem(position);

        BitmapReference bitmapRef;

        if (news.getImageLink() == null || TextUtils.isEmpty(news.getImageLink())) {
            bitmapRef = ImageDao.getNotAvailableImage();
        } else {
            bitmapRef = imageDao.read(news.getImageLink());
        }

        ViewHolder holder;
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_list_item, null);
			newsTitleTextView = (TextView)view.findViewById(R.id.brief_news_title);
            newsDateTextView = (TextView)view.findViewById(R.id.brief_news_date);
            newsCaptionTextView = (TextView)view.findViewById(R.id.brief_news_image_caption);
            newsCreditsTextView = (TextView)view.findViewById(R.id.brief_news_image_credits);
            newsDescriptionTextView = (TextView)view.findViewById(R.id.brief_news_description);
            newsDescriptionPanel = view.findViewById(R.id.brief_news_description_panel);

            newsImageView = (ImageView)view.findViewById(R.id.brief_news_image);

            final View newsDescriptionPanelAsync = newsDescriptionPanel;
            view.setTag(holder = new ViewHolder(newsImageView, newsTitleTextView, newsDescriptionTextView, newsDateTextView,
                    newsCaptionTextView, newsCreditsTextView, newsDescriptionPanel, position));

            newsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newsDescriptionPanelAsync.getVisibility() == View.GONE) {
                        newsDescriptionPanelAsync.setVisibility(View.VISIBLE);
                    } else {
                        newsDescriptionPanelAsync.setVisibility(View.GONE);
                    }
                }
            });
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();

            AsyncTask asyncTask = holder.getAsyncTask();
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }

            holder.setPosition(position);

			newsTitleTextView = holder.getNewsTitle();
            newsDescriptionTextView = holder.getNewsDescription();
            newsDateTextView = holder.getNewsDate();
            newsCaptionTextView = holder.getNewsImageCaption();
            newsCreditsTextView = holder.getNewsImageCredits();
			newsImageView = holder.getNewsImage();
            newsDescriptionPanel = holder.getNewsDescriptionPanel();

            newsDescriptionPanel.setVisibility(View.GONE);

//            BitmapReference prevImageRef = holder.getImage();
//			if (prevImageRef != null) {
//				prevImageRef.releaseBitmap();
//			}
		}

        holder.setImage(bitmapRef);
        holder.setImageUrl(news.getImageLink());

		newsTitleTextView.setText(news.getTitle());
        newsDescriptionTextView.setText(Html.fromHtml(news.getDescription()));

        if (news.getImageCaption() == null || TextUtils.isEmpty(news.getImageCaption())) {
            newsCaptionTextView.setVisibility(View.GONE);
        } else {
            newsCaptionTextView.setText(news.getImageCaption());
            newsCaptionTextView.setVisibility(View.VISIBLE);
        }

        if (news.getImageCredits() == null || TextUtils.isEmpty(news.getImageCredits())) {
            newsCreditsTextView.setVisibility(View.GONE);
        } else {
            newsCreditsTextView.setText(Html.fromHtml(news.getImageCredits()));
            newsCreditsTextView.setVisibility(View.VISIBLE);
        }

        newsDateTextView.setText(news.getFormattedPubDate());

		final ImageView newsImageViewForAsync = newsImageView;
        final ViewHolder holderForAsync = holder;
        final String imageUrl = news.getImageLink();

        // Set loading bitmap now, but only if we are really going to load news bitmap from internet
        if (bitmapRef != ImageDao.getNotAvailableImage()) {
            newsImageView.setImageBitmap(ImageDao.getLoadingImage().getBitmap());
        }

        final AsyncTask<BitmapReference.Callback, Void, Bitmap> asyncTask = bitmapRef.getBitmapAsync(new BitmapReference.Callback() {
			@Override
			public void onSuccess(Bitmap bitmap) {
                if (bitmap == null ||
                        position != holderForAsync.getPosition() ||
                        (imageUrl == null && holderForAsync.getImage() != ImageDao.getNotAvailableImage()) ||
                        (imageUrl != null && !imageUrl.equals(holderForAsync.getImageUrl()))) {
                    return;
                }

                newsImageViewForAsync.setImageBitmap(bitmap);
			}

            @Override
            public void onFailure() {
                newsImageViewForAsync.setImageBitmap(ImageDao.getNotAvailableImage().getBitmap());
            }
        });

        holder.setAsyncTask(asyncTask);
		
		return view;
	}
}
