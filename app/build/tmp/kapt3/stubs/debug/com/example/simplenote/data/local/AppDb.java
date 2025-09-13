package com.example.simplenote.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00052\u00020\u0001:\u0001\u0005B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&\u00a8\u0006\u0006"}, d2 = {"Lcom/example/simplenote/data/local/AppDb;", "Landroidx/room/RoomDatabase;", "()V", "notes", "Lcom/example/simplenote/data/local/NoteDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.example.simplenote.data.local.NoteEntity.class}, version = 1, exportSchema = false)
public abstract class AppDb extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.simplenote.data.local.AppDb.Companion Companion = null;
    
    public AppDb() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.simplenote.data.local.NoteDao notes();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/example/simplenote/data/local/AppDb$Companion;", "", "()V", "create", "Lcom/example/simplenote/data/local/AppDb;", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.simplenote.data.local.AppDb create(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}