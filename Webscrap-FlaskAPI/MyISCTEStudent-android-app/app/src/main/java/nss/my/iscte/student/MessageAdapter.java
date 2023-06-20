package nss.my.iscte.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> implements Filterable {

	private List<Message> messageList;
	private Context context;
	private List<Message> messageListFiltered;
	private MessageAdapterListener listener;


	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView sFrom, sSubject, sDate, sMsg,avatar;
		//public ImageView avatar;

		public MyViewHolder(View view) {
			super(view);
			sFrom = (TextView) view.findViewById(R.id.txtSendBY);
			sSubject = (TextView) view.findViewById(R.id.txtTitle);
			sDate = (TextView) view.findViewById(R.id.txtDate);
			avatar = (TextView) view.findViewById(R.id.avatar);
			sMsg = (TextView) view.findViewById(R.id.txtEmailDetails);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// send selected contact in callback
					listener.onMessageSelected(messageListFiltered.get(getAdapterPosition()));
				}
			});

		}
	}


	public MessageAdapter(Context context, List<Message> messageList, MessageAdapterListener listener) {
		this.messageList = messageList;
		this.context = context;
		this.listener = listener;
		this.messageListFiltered = messageList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_mail_item, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		//Message message = messageList.get(position);
		final Message message = messageListFiltered.get(position);
		holder.avatar.setText(message.getsFrom().substring(0,1));
		holder.sSubject.setText(message.getsSubject());
		holder.sFrom.setText(message.getsFrom());
		holder.sDate.setText(message.getsDate());
		final String sHtml = message.getsMsg();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			holder.sMsg.setText(Html.fromHtml(message.getsMsg(), Html.FROM_HTML_MODE_COMPACT));
		} else {
			holder.sMsg.setText(Html.fromHtml(message.getsMsg()));
		}

		Random rnd = new Random();
		final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
		((GradientDrawable) holder.avatar.getBackground()).setColor(color);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(context, MessageDetails.class);
				mIntent.putExtra("sender", holder.sFrom.getText().toString());
				mIntent.putExtra("title", holder.sSubject.getText().toString());
				mIntent.putExtra("details", holder.sMsg.getText().toString());
				mIntent.putExtra("date", holder.sDate.getText().toString());
				mIntent.putExtra("avatar", holder.avatar.getText().toString());
				mIntent.putExtra("colorAvatar", color);
				mIntent.putExtra("messageHTML", sHtml);
				context.startActivity(mIntent);
			}
		});


		//TextDrawable avatarText = TextDrawable.builder().beginConfig()
		//		.width(40)  // width in px
		//			.height(40) // height in px
		//			.endConfig().buildRound(message.sFrom.substring(0,1), Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))); // radius in px
		//holder.avatar.setImageDrawable(avatarText);
		//holder.avatar.setImageResource(R.drawable.ic_icon_avatar);
	}

	@Override
	public int getItemCount() {
		return messageListFiltered.size();
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				String charString = charSequence.toString();
				if (charString.isEmpty()) {
					messageListFiltered = messageList;
				} else {
					List<Message> filteredList = new ArrayList<>();
					for (Message row : messageList) {

						// name match condition. this might differ depending on your requirement
						// here we are looking for name or phone number match
						if (row.getsFrom().toLowerCase().contains(charString.toLowerCase()) || row.getsSubject().contains(charSequence)) {
							filteredList.add(row);
						}
					}

					messageListFiltered = filteredList;
				}

				FilterResults filterResults = new FilterResults();
				filterResults.values = messageListFiltered;
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
				messageListFiltered = (ArrayList<Message>) filterResults.values;
				notifyDataSetChanged();
			}
		};
	}

	public interface MessageAdapterListener {
		void onMessageSelected(Message message);
	}

}
