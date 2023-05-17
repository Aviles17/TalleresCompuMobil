package com.example.taller_3v2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taller_3v2.ListaDisponiblesActivity;
import com.example.taller_3v2.listeners.UserListener;
import com.example.taller_3v2.modelo.Usuario;
import com.example.taller_3v2.databinding.ItemConteinerUserBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>  {

    private final List<Usuario> usuarioList;

    private final UserListener userListener;
    private Context context;
    private View.OnClickListener listener;

    public UsersAdapter(List<Usuario> usuarioList, ListaDisponiblesActivity userListener) {
        this.usuarioList = usuarioList;
        this.userListener = (UserListener) userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConteinerUserBinding itemContainerUserBinding = ItemConteinerUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        holder.setUserData(usuarioList.get(position));
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemConteinerUserBinding binding;
        UserViewHolder(ItemConteinerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }
        void setUserData(Usuario user){
            binding.TextName.setText(user.getNombre());
            binding.imageprofile.setImageBitmap(getUserImage(user.getFotodeperfil()));
            binding.mostrarBTN.setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String image){
        byte[] decodedString = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
