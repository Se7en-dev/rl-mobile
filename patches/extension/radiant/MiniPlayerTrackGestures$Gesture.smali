.class public final Lradiant/MiniPlayerTrackGestures$Gesture;
.super Ljava/lang/Object;
.implements Lyl0/l;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lradiant/MiniPlayerTrackGestures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "Gesture"
.end annotation


# instance fields
.field public final a:Lyl0/l;

# X at ACTION_DOWN
.field public b:F

# Y at ACTION_DOWN
.field public c:F

.field public d:Z


# direct methods
.method public constructor <init>(Lyl0/l;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->a:Lyl0/l;

    return-void
.end method

.method private static dp(F)F
    .locals 1

    invoke-static {}, Landroid/content/res/Resources;->getSystem()Landroid/content/res/Resources;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v0

    iget v0, v0, Landroid/util/DisplayMetrics;->density:F

    mul-float/2addr p0, v0

    return p0
.end method

.method private nextTrack()V
    .locals 2

    iget-object v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->a:Lyl0/l;

    sget-object v1, Lcom/tidal/android/feature/appscaffold/ui/q$a;->a:Lcom/tidal/android/feature/appscaffold/ui/q$a; # Tidal's built-in next track mini-player event

    invoke-interface {v0, v1}, Lyl0/l;->invoke(Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method

.method private previousTrack()V
    .locals 2

    iget-object v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->a:Lyl0/l;

    sget-object v1, Lcom/tidal/android/feature/appscaffold/ui/q$c;->a:Lcom/tidal/android/feature/appscaffold/ui/q$c; # Our synthetic previous track event

    invoke-interface {v0, v1}, Lyl0/l;->invoke(Ljava/lang/Object;)Ljava/lang/Object;

    return-void
.end method


# virtual methods
.method public final invoke(Landroid/view/MotionEvent;)Lkotlin/u;
    .locals 6

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getActionMasked()I

    move-result v0

    if-eqz v0, :down

    const/4 v1, 0x1

    if-eq v0, v1, :up

    const/4 v1, 0x3

    if-eq v0, v1, :cancel

    goto :done

    # ACTION_DOWN
    :down
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getRawX()F

    move-result v0

    iput v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->b:F

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getRawY()F

    move-result v0

    iput v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->c:F

    const/4 v0, 0x1

    iput-boolean v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->d:Z

    goto :done

    # ACTION_UP
    :up
    iget-boolean v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->d:Z

    if-eqz v0, :done

    const/4 v0, 0x0

    iput-boolean v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->d:Z

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getRawX()F

    move-result v0

    iget v1, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->b:F

    sub-float/2addr v0, v1

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getRawY()F

    move-result v1

    iget v2, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->c:F

    sub-float/2addr v1, v2

    invoke-static {v0}, Ljava/lang/Math;->abs(F)F

    move-result v2

    const/high16 v3, 0x42400000    # 48.0f -> at least 48dp horizontal swipe

    invoke-static {v3}, Lradiant/MiniPlayerTrackGestures$Gesture;->dp(F)F

    move-result v3

    cmpg-float v4, v2, v3

    if-gez v4, :check_axis

    goto :done

    :check_axis
    invoke-static {v1}, Ljava/lang/Math;->abs(F)F

    move-result v1

    const/high16 v3, 0x3fc00000    # 1.5f -> horizontal distance must dominate vertical mov. by 1.5x

    mul-float/2addr v1, v3

    cmpg-float v2, v2, v1

    if-lez v2, :done

    const/4 v1, 0x0

    cmpg-float v0, v0, v1

    # Negative dx = swipe left -> next. Positive dx = swipe right -> previous
    if-ltz v0, :next

    invoke-static {}, Lradiant/MiniPlayerTrackGestures;->suppressTapOpen()V

    invoke-direct {p0}, Lradiant/MiniPlayerTrackGestures$Gesture;->previousTrack()V

    goto :done

    :next
    invoke-static {}, Lradiant/MiniPlayerTrackGestures;->suppressTapOpen()V

    invoke-direct {p0}, Lradiant/MiniPlayerTrackGestures$Gesture;->nextTrack()V

    goto :done

    # ACTION_CANCEL
    :cancel
    const/4 v0, 0x0

    iput-boolean v0, p0, Lradiant/MiniPlayerTrackGestures$Gesture;->d:Z

    :done
    sget-object p1, Lkotlin/u;->a:Lkotlin/u;

    return-object p1
.end method

.method public bridge synthetic invoke(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    check-cast p1, Landroid/view/MotionEvent;

    invoke-virtual {p0, p1}, Lradiant/MiniPlayerTrackGestures$Gesture;->invoke(Landroid/view/MotionEvent;)Lkotlin/u;

    move-result-object p1

    return-object p1
.end method
