package cz.fit.lentaruand.ui.fragments;

import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.BitmapReference;

public class NewsAdapter extends NewsObjectAdapter<News> {
	
	private static class ViewHolder {
		private final TextView newsTitle;
		private final ImageView newsImage;
		private final BitmapReference imageRef;
		
		public ViewHolder(ImageView newsImage, TextView newsTitle, BitmapReference imageRef) {
			this.newsImage = newsImage;
			this.newsTitle = newsTitle;
			this.imageRef = imageRef;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}

		public ImageView getNewsImage() {
			return newsImage;
		}
		
		public BitmapReference getImage() {
			return imageRef;
		}
	}

	public NewsAdapter(Context context, Collection<News> newsObjects) {
		super(context, newsObjects);
	}

	public NewsAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ImageView newsImageView;
		TextView newsTitleTextView;
		BitmapReference imageRef;

		News news = getItem(position);
		imageRef = news.getImage();
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_list_item, null);
			newsTitleTextView = (TextView)view.findViewById(R.id.brief_news_title);
			newsImageView = (ImageView)view.findViewById(R.id.brief_news_image);
			
			view.setTag(new ViewHolder(newsImageView, newsTitleTextView, imageRef));
		} else {
			view = convertView;
			ViewHolder holder = (ViewHolder)view.getTag();
			
			newsTitleTextView = holder.getNewsTitle();
			newsImageView = holder.getNewsImage();
			holder.getImage().releaseBitmap();
		}

		Bitmap currBitmap = imageRef.getBitmap();
		
		if (currBitmap != null) {
			newsImageView.setImageBitmap(currBitmap);
		}
		
		newsTitleTextView.setText(news.getTitle());
		return view;
	}
}
