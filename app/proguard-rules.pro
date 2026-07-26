# Add project specific ProGuard rules here.
-keepclassmembers class com.example.billiardgame.data.local.** { *; }
-keepclassmembers enum com.example.billiardgame.data.model.CueStickTier {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.example.billiardgame.domain.model.Ball { *; }
-keep class com.example.billiardgame.domain.model.GameState { *; }
-keep class com.example.billiardgame.engine.PhysicsEngine { *; }
