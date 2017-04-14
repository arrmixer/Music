package com.AE.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angel on 4/12/17.
 */
public class Datasource {
    public static final String DB_NAME = "music.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:/Users/Angel/git/Music/" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;


    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COlUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TITLE = 2;
    public static final int INDEX_SONG_TRACK = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String QUERY_ALBUMS_BY_ARTIST_START =
            " SELECT " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " FROM " +
                    TABLE_ALBUMS + " INNER JOIN " + TABLE_ARTISTS + " ON " +
                    TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS +
                    "." + COLUMN_ARTIST_ID + " WHERE " +
                    TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + " = \"";

    public static final String QUERY_ALBUMS_BY_ARTIST_SORT =
            " ORDER BY " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME +
                    " COLLATE NOCASE ";
    public static final String QUERY_ARTISTS_START = " SELECT * FROM " + TABLE_ARTISTS;
    public static final String QUERY_ARTISTS_SORT = " ORDER BY " + COLUMN_ARTIST_NAME + " COLLATE NOCASE ";

    public static final String QUERY_ARTIST_FOR_SONG_START = " SELECT " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME +
        ", " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " + TABLE_SONGS + "." + COlUMN_SONG_TRACK + " FROM " +
        TABLE_SONGS + " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS + "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS
        + "." + COLUMN_ALBUM_ID + " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " +
        TABLE_ARTISTS + "." + COLUMN_ARTIST_ID + " WHERE " + TABLE_SONGS + "." + COLUMN_SONG_TITLE + " = \"";
    public static final String  QUERY_ARTIST_FOR_SONG_SORT = " ORDER BY " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
            TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME +
            " COLLATE NOCASE ";






    private Connection conn;

    public boolean open(){
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        }catch(SQLException e){
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;

        }
    }

    public void close(){
        try{
            if(conn != null){
                conn.close();
            }
        }catch(SQLException e){
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public List<Artist> queryArtists(int sortOrder){

        StringBuilder sb = new StringBuilder(QUERY_ARTISTS_START);

     orderBY(sb, QUERY_ARTISTS_SORT, sortOrder);
        //try with resources will call the close methods for both statement and resultsSet.
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sb.toString())){



            List<Artist> artists = new ArrayList<>();
            while(results.next()){
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }

            return artists;
        }catch(SQLException e){
            System.out.println("Query falied: " + e.getMessage());
            return null;
        }
    }

    public List<String> queryAlbumsForArtist(String artistName, int sortOrder){

//        SELECT albums.name FROM albums INNER JOIN artists ON albums.artist = artists._id WHERE artists.name = "Carole King"
//        ORDER BY albums.name COLLATE NOCASE ASC
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTIST_START);
        sb.append(artistName);
        sb.append("\"");


      orderBY(sb, QUERY_ALBUMS_BY_ARTIST_SORT, sortOrder);

        System.out.println("SQL statement = " + sb.toString());

        try (Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){

            List<String> albums = new ArrayList<>();
            while(results.next()){
                albums.add(results.getString(1));
            }

            return  albums;
        }catch (SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            e.getStackTrace();
            return null;
        }


    }

    public List<SongArtist> queryArtistsForSong(String song, int sortOrder){
//    SELECT artists.name, albums.name, songs.track FROM songs INNER JOIN albums ON
//    songs.album = albums._id
//    INNER JOIN artists ON albums.artist = artists._id
//    WHERE songs.title = "Go Your Own Way"
//    ORDER BY artists.name, albums.name COLLATE NOCASE ASC
        StringBuilder sb = new StringBuilder(QUERY_ARTIST_FOR_SONG_START);
        sb.append(song);
        sb.append("\"");

        orderBY(sb, QUERY_ARTIST_FOR_SONG_SORT, sortOrder);

        System.out.println("SQL statement = " + sb.toString());

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())){

            List<SongArtist> songArtists = new ArrayList<>();
            while(results.next()){
                SongArtist songArtist = new SongArtist();
                songArtist.setTrack(results.getInt(3));
                songArtist.setAlbumName(results.getString(2));
                songArtist.setArtistName(results.getString(1));
                songArtists.add(songArtist);

            }

            return songArtists;
        }catch (SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            e.getStackTrace();
            return null;
        }




    }

    public void orderBY(StringBuilder sb, String query, int sortOrder){
        if(sortOrder != ORDER_BY_NONE){
            sb.append(query);
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            }else{
                sb.append("ASC");
            }
        }
    }

public void querySongsMetadata(){
        String sql = " SELECT * FROM " + TABLE_SONGS;

        try (Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sql)) {

            ResultSetMetaData meta = results.getMetaData();
            int numColumns = meta.getColumnCount();
            for(int i=1;i<= numColumns; i++){
                System.out.format("Column %d in the songs table is names %s\n",
                        i, meta.getColumnName(i));
            }
        }catch (SQLException e){
            System.out.println("Query fail: " + e.getMessage());
        }
    }

    public int getCount(String table){
    String sql = " SELECT COUNT(*) AS count FROM " + table;
    try(Statement statement = conn.createStatement();
    ResultSet results = statement.executeQuery(sql)){
        int count = results.getInt("count");
        System.out.format("Count = %d\n", count );
        return count;
    }catch(SQLException e){
        System.out.println("Query failed: " + e.getMessage());
        return -1;
    }
    }
}