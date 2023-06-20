package nss.my.iscte.student;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

	private List<Avaliacao> avaliacaoList;
	private Context context;
	private AvaliacaoAdapterListener listener;

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView disciplina,nota;
		public ImageView simbolGrade;
		public ProgressBar pBar;

		public MyViewHolder(View view) {
			super(view);
			disciplina = (TextView) view.findViewById(R.id.txtDisciplina);
			nota = (TextView) view.findViewById(R.id.txtNota);
			simbolGrade = (ImageView) view.findViewById(R.id.simbolGrade);
			pBar = (ProgressBar) view.findViewById(R.id.txtProgress);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//TODO
				}
			});

		}
	}


	public MyAdapter(Context context, List<Avaliacao> avaliacaoList, AvaliacaoAdapterListener listener) {
		this.avaliacaoList = avaliacaoList;
		this.context = context;
		this.listener = listener;

	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.avaliacao_listview, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		final Avaliacao avaliacao = avaliacaoList.get(position);
		holder.disciplina.setText(avaliacao.getDisciplina());
		holder.nota.setText(avaliacao.getNota());
		holder.pBar.setProgress(avaliacao.getpBar());
		holder.pBar.setScaleY(2);

		if (avaliacao.getpBar() > 9) {
			holder.simbolGrade.setImageResource(R.drawable.ic_check_black_24dp);
		}else if (avaliacao.getpBar() > 0){
			holder.simbolGrade.setImageResource(R.drawable.ic_clear_black_24dp);
		}else{
			holder.simbolGrade.setImageResource(R.drawable.ic_hourglass_full_black_24dp);
		}
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO:
			}
		});
	}

	@Override
	public int getItemCount() {
		return avaliacaoList.size();
	}

	public interface AvaliacaoAdapterListener {
		void onAvaliacaoSelected(Avaliacao avaliacao);
	}
}
