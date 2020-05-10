package com.hiBud.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hiBud.app.DAO.UsuarioDAO;
import com.hiBud.app.Holder.MensajeHolder;
import com.hiBud.app.Logic.LMensaje;
import com.hiBud.app.Logic.LUsuario;
import com.hiBud.app.R;

import java.util.ArrayList;
import java.util.List;

public class MensajeAdapter<MensajesHolderHolder> extends RecyclerView.Adapter<MensajeHolder> {

    private List<LMensaje> listMensaje = new ArrayList<>();
    private Context c;

    public MensajeAdapter(Context c) {
        this.c = c;
    }

    public int addMensaje(LMensaje lMensaje) {
        listMensaje.add(lMensaje);
        //3 mensajes
        int posicion = listMensaje.size() - 1;//3
        notifyItemInserted(listMensaje.size());
        return posicion;
    }

    public void actualizarMensaje(int posicion, LMensaje lMensaje) {
        listMensaje.set(posicion, lMensaje);//2
        notifyItemChanged(posicion);
    }

    @NonNull
    @Override
    public MensajeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_enviados, parent, false);
        } else {
            view = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_recibidos, parent, false);
        }
        return new MensajeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeHolder holder, int position) {

        LMensaje lMensaje = listMensaje.get(position);

        LUsuario lUsuario = lMensaje.getlUsuario();

        if (lUsuario != null) {
            holder.getNombre().setText(lUsuario.getUsuario().getNombre());
            Glide.with(c).load(lUsuario.getUsuario().getFotoPerfilURL()).into(holder.getFotoMensajePerfil());
        }

        holder.getMensaje().setText(lMensaje.getMensaje().getMensaje());
        if (lMensaje.getMensaje().isContieneFoto()) {
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(lMensaje.getMensaje().getUrlFoto()).into(holder.getFotoMensaje());
        } else {
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }

        holder.getHora().setText(lMensaje.fechaDeCreacionDelMensaje());
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (listMensaje.get(position).getlUsuario() != null) {
            if (listMensaje.get(position).getlUsuario().getKey().equals(UsuarioDAO.getInstancia().getKeyUsuario())) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}