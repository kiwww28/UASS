package com.example;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) {
        try {
            JFrame jFrame = new JFrame(); // instansiasi JFrame
            jFrame.setTitle("Pokemon");
            jFrame.setSize(600, 800); // Ukuran JFrame yang lebih pas untuk menampung tabel
            jFrame.setLocationRelativeTo(null); // Agar aplikasi nya di tengah
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // agar saat di klik close aplikasi berhenti

            JPanel panel = new JPanel(new BorderLayout()); // instansiasi JPanel

            HttpClient client = HttpClient.newHttpClient(); // instansiasi HttpClient
            Gson gson = new Gson(); // instansiasi Gson
            String url = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=30";
            URI uri = new URI(url); // instansiasi URI

            HttpRequest request = HttpRequest.newBuilder() // Membuat request
                    .uri(uri)
                    .GET()
                    .build();

            // menangkap response dari client
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // merubah response dari string jadi objek
            Pokemon jsonObject = gson.fromJson(response.body(), Pokemon.class);

            String[] columnNames = { "No", "Name" }; // Membuat kolom No dan Name
            Object[][] data = new Object[jsonObject.results.length][2];

            for (int i = 0; i < jsonObject.results.length; i++) {
                Pokemons details = jsonObject.results[i];
                data[i][0] = i + 1; // mengisi kolom nomer dimulai dari angka 1
                data[i][1] = details.name; // mengisi kolom nama sesuai dengan objek detail yg di isi
            }

            // membuat tabel model dari data dan kolom yg sudah di buat
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

            // membuat tabel dari tabel model
            JTable table = new JTable(tableModel);

            // membuat scroll pane untuk tabel agar bisa di scroll
            JScrollPane scrollPane = new JScrollPane(table);

            // menambahkan scroll panel nya ke panel dan mengubah posisinya agar di tengah
            panel.add(scrollPane, BorderLayout.CENTER);

            // membuat panel untuk detail
            JPanel detailPanel = new JPanel(new FlowLayout());

            // instansiasi label untuk nama
            JLabel nameLabel = new JLabel("Klik sebuah baris untuk melihat nama Pokémon");

            // menambah label nama ke detail panel
            detailPanel.add(nameLabel);

            // menambahkan detail panel nya ke panel sekaligus memposisikannya
            panel.add(detailPanel, BorderLayout.SOUTH);

            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String namaPokemon = table.getValueAt(row, 1).toString(); // ambil nama dari value table
                        String nomerPokemon = table.getValueAt(row, 0).toString(); // ambil nomor dari value table

                        // link gambar pokemon
                        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
                                + nomerPokemon + ".png";

                        try {
                            // Mengunduh gambar dari URL
                            BufferedImage image = ImageIO.read(new URL(imageUrl));
                            if (image != null) {
                                ImageIcon icon = new ImageIcon(image); // instansiasi imageicon
                                JLabel picLabel = new JLabel(icon); // instansiasi gambar sebagai jlabel
                                detailPanel.removeAll(); // Menghapus komponen lama dari panel
                                detailPanel.add(nameLabel); // Menambah label nama kembali
                                detailPanel.add(picLabel); // Menambah gambar Pokémon
                                detailPanel.revalidate(); // Mereset tampilan
                                detailPanel.repaint(); // Menggambar ulang
                            }

                            // melakukan request lagi ke url details pokemon
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(new URI("https://pokeapi.co/api/v2/pokemon/" + namaPokemon))
                                    .GET()
                                    .build();

                            // menangkap response yg di kembalikan client
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                            // mengubah response string menjadi objek
                            PokemonDetails details = gson.fromJson(response.body(), PokemonDetails.class);

                            // instansiasi String builder
                            StringBuilder types = new StringBuilder("Types: ");
                            for (Types type : details.types) {
                                // biar semua type pokemonnya di gabungin ke 1 variable dan di pisah pake koma ,
                                types.append(type.type.name).append(", ");
                            }

                            // menimpa tulisan Klik sebuah baris untuk melihat nama Pokémon menjadi Type
                            // pokemon
                            nameLabel.setText(types.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            // tambah panel ke Jframe
            jFrame.add(panel);

            // tampilin deh
            jFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
