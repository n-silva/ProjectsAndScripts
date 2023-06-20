package nss.my.iscte.student;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FragmentHome extends Fragment implements MyAdapter.AvaliacaoAdapterListener{

	protected MyISCTEDatasource ds;
	private String mail, password;
	private ImageButton avatar;
	private TextView name, curso, info;
	private LinearLayout linearLayout;
	RecyclerView lstAvalicao;
	ArrayList<String> infoArray = new ArrayList<String>();
	private List<Avaliacao> avaliacaoList = new ArrayList<>();
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	SwipeRefreshLayout swipeRefreshLayout ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		mail = settings.getString("email", "");
		password = settings.getString("password", "");

		avatar = (ImageButton) view.findViewById(R.id.user_profile_photo);
		name = (TextView) view.findViewById(R.id.user_profile_name);
		curso = (TextView) view.findViewById(R.id.user_profile_short_bio);
		info = (TextView) view.findViewById(R.id.user_profile_notes);
		lstAvalicao = (RecyclerView) view.findViewById(R.id.avaliacao_list);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		lstAvalicao.setHasFixedSize(true);
		avaliacaoList = new ArrayList<>();
		adapter = new MyAdapter(getContext(), avaliacaoList, this);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		lstAvalicao.setLayoutManager(mLayoutManager);
		lstAvalicao.setItemAnimator(new DefaultItemAnimator());
		lstAvalicao.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
		lstAvalicao.setAdapter(adapter);

		getUserDetails();

		return view;
	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.menu, menu);
		MenuItem searchItem = menu.findItem(R.id.menuSearch);
		searchItem.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public static FragmentHome newInstance() {
		return new FragmentHome();
	}

	public  void getUserDetails(){
		ds = new MyISCTEDatasource(getContext());
		ds.open();

		User us = ds.getUserByEmail(mail);
		String imageString = us.getAvatar();
		byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
		Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		avatar.setImageBitmap(decodedImage);
		name.setText(us.getName());
		curso.setText(us.getCurso());
		info.setText("Aluno nº: " +us.getNumero() + "      Data de matrícula: " + us.getDatamatricula());
		Avaliacao cAval;
		try {
			JSONArray avaliacao = new JSONArray(us.getAvaliacao());
			for (int i = 0; i < avaliacao.length(); i++)
			{
				JSONObject jsonObj = avaliacao.getJSONObject(i);
				Integer pbar = 0;
				try {
					pbar = Integer.parseInt(jsonObj.getString("grade"));
				} catch(NumberFormatException nfe) {
					pbar = 0;
				}
				cAval = new Avaliacao(jsonObj.getString("class") ,jsonObj.getString("grade"), pbar);
				avaliacaoList.add(cAval);
			}

			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAvaliacaoSelected(Avaliacao avaliacao) {

	}
}

class Avaliacao{
	protected  String disciplina , nota;
	protected  Integer pBar;

	public Avaliacao(String disciplina, String nota, int pBar) {
		this.disciplina = disciplina;
		this.nota = nota;
		this.pBar = pBar;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public Integer getpBar() {
		return pBar;
	}

	public void setpBar(Integer pBar) {
		//if (!Integer.parseInt(String.valueOf(pBar))) {
			this.pBar = pBar;
		//}

	}
}
