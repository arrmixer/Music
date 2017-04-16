package com.AE;

import com.AE.Model.Artist;
import com.AE.Model.Datasource;
import com.AE.Model.SongArtist;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        if(!datasource.open()){
            System.out.println("Can't open datasource");
            return;
        }

        List<Artist> artists = datasource.queryArtists(Datasource.ORDER_BY_NONE);
        if(artists == null){
            System.out.println("No artists!");
            return;
        }

        for(Artist artist : artists){
            System.out.println("ID = " + artist.getId() + ", Name = " + artist.getName());
        }

        List<String> albumsForArtist = datasource.queryAlbumsForArtist("Chemical Brothers", Datasource.ORDER_BY_ASC);

        for(String album : albumsForArtist){
            System.out.println(album);
        }

        List<SongArtist> artistsForSong = datasource.queryArtistsForSong("Go Your Own Way", Datasource.ORDER_BY_ASC);
        if(artistsForSong == null){
            System.out.println("No songs!");
            return;
        }

        for(SongArtist songArtist : artistsForSong){
            System.out.println("Artist name: " + songArtist.getArtistName() + ",  Album name: " + songArtist.getAlbumName() + ", Track number: " + songArtist.getTrack());
        }

        datasource.querySongsMetadata();

        datasource.getCount(Datasource.TABLE_SONGS);

        datasource.createViewForSongArtists();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter song title: ");

//        String title = scanner.nextLine();
//
//        artistsForSong = datasource.querySongInfoView(title);
//
//        if(artistsForSong.isEmpty()){
//            System.out.println("No songs!");
//            return;
//        }
//
//        for(SongArtist songArtist : artistsForSong){
//            System.out.println("From View: Artist name: " + songArtist.getArtistName() + ",  Album name: " + songArtist.getAlbumName() + ", Track number: " + songArtist.getTrack());
//        }

        datasource.insertSong("Like A Rolling Stone", "Bob Dylan", "Bob Dylan's Greatest Hits", 5);

        datasource.close();
    }
}
