package nss.my.iscte.student;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

	private List<MyEvents> eventsList;
	private Context context;
	private EventAdapterListener listener;

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView data,hora,evento;
		public ImageView avatar;

		public MyViewHolder(View view) {
			super(view);
			data = (TextView) view.findViewById(R.id.txtEventData);
			hora = (TextView) view.findViewById(R.id.txtEventHora);
			evento = (TextView) view.findViewById(R.id.txtEvent);
			avatar = (ImageView) view.findViewById(R.id.eventAvatar);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//TODO
				}
			});

		}
	}


	public EventAdapter(Context context, List<MyEvents> eventList, EventAdapterListener listener) {
		this.eventsList = eventList;
		this.context = context;
		this.listener = listener;

	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_calendar_item, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		final MyEvents event = eventsList.get(position);
		holder.data.setText(event.getData());
		holder.hora.setText(event.getHora());
		holder.evento.setText(event.getEvento());
		holder.avatar.setImageResource(R.drawable.ic_notifications_black_24dp);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO:
			}
		});
	}

	@Override
	public int getItemCount() {
		return eventsList.size();
	}

	public interface EventAdapterListener {
		void onEventSelected(MyEvents event);
	}

}
