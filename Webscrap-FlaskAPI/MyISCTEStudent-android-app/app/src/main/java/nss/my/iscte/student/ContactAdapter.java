package nss.my.iscte.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> implements Filterable {

	private List<Contact> contactList;
	private Context context;
	private List<Contact> contactListFiltered;
	private ContactAdapterListener listener;
	final int EMPTY_VIEW = 77777;
	private View itemView;

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView name, email, type;
		public ImageView avatar;

		public MyViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.txtPersonName);
			email = (TextView) view.findViewById(R.id.txtEmail);
			type = (TextView) view.findViewById(R.id.txtTipo);
			avatar = (ImageView) view.findViewById(R.id.contactAvatar);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// send selected contact in callback
					listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
				}
			});

		}
	}


	public ContactAdapter(Context context, List<Contact> contactList, ContactAdapterListener listener) {
		this.contactList = contactList;
		this.context = context;
		this.listener = listener;
		this.contactListFiltered = contactList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//if (viewType == EMPTY_VIEW) {
		//	itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty, parent, false);
		//} else {
		//	itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
		//}
		itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
		return new MyViewHolder(itemView);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		//if (getItemViewType(position) != EMPTY_VIEW){
			final Contact contact = contactListFiltered.get(position);
			String imageString = contact.getAvatar();
			byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
			Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			holder.avatar.setImageBitmap(decodedImage);

			holder.name.setText(contact.getName());
			holder.email.setText(contact.getEmail());
			holder.type.setText(contact.getType());

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO: implementar activity detalhe do contacto
				}
			});
		//}

	}

	@Override
	public int getItemCount() {
		return contactListFiltered.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (contactListFiltered.size() == 0) {
			return EMPTY_VIEW;
		}
		return super.getItemViewType(position);
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				String charString = charSequence.toString();
				if (charString.isEmpty()) {
					contactListFiltered = contactList;
				} else {
					List<Contact> filteredList = new ArrayList<>();
					for (Contact row : contactList) {
						if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getType().contains(charSequence)) {
							filteredList.add(row);
						}
					}

					contactListFiltered = filteredList;
				}

				FilterResults filterResults = new FilterResults();
				filterResults.values = contactListFiltered;
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
				contactListFiltered = (ArrayList<Contact>) filterResults.values;
				notifyDataSetChanged();
			}
		};
	}

	public interface ContactAdapterListener {
		void onContactSelected(Contact contact);
	}

}
