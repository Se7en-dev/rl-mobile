.class public final Lradiant/MiniPlayerTrackGestures;
.super Ljava/lang/Object;


# static fields
.field private static a:J


# direct methods
.method private constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static consumeTapOpenSuppression()Z
    .locals 5

    sget-wide v0, Lradiant/MiniPlayerTrackGestures;->a:J

    const-wide/16 v2, 0x0

    cmp-long v4, v0, v2

    if-nez v4, :check_time

    const/4 v0, 0x0

    return v0

    :check_time
    sput-wide v2, Lradiant/MiniPlayerTrackGestures;->a:J

    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v2

    cmp-long v4, v2, v0

    if-lez v4, :yes

    const/4 v0, 0x0

    return v0

    :yes
    const/4 v0, 0x1

    return v0
.end method

.method public static suppressTapOpen()V
    .locals 4

    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v0

    const-wide/16 v2, 0x258

    add-long/2addr v0, v2

    sput-wide v0, Lradiant/MiniPlayerTrackGestures;->a:J

    return-void
.end method

.method public static trackModifier(Landroidx/compose/ui/Modifier;Lyl0/l;)Landroidx/compose/ui/Modifier;
    .locals 1

    new-instance v0, Lradiant/MiniPlayerTrackGestures$Gesture;

    invoke-direct {v0, p1}, Lradiant/MiniPlayerTrackGestures$Gesture;-><init>(Lyl0/l;)V

    invoke-static {p0, v0}, Landroidx/compose/ui/input/pointer/PointerInteropFilter_androidKt;->motionEventSpy(Landroidx/compose/ui/Modifier;Lyl0/l;)Landroidx/compose/ui/Modifier;

    move-result-object p0

    return-object p0
.end method
