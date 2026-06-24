.class public final Lradiant/MiniPlayerGestures$RootGesture;
.super Ljava/lang/Object;
.implements Lyl0/l;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lradiant/MiniPlayerGestures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "RootGesture"
.end annotation


# instance fields
.field public final a:Lyl0/l;

.field public final b:Landroidx/compose/material3/SheetState;


# direct methods
.method public constructor <init>(Lyl0/l;Landroidx/compose/material3/SheetState;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lradiant/MiniPlayerGestures$RootGesture;->a:Lyl0/l;

    iput-object p2, p0, Lradiant/MiniPlayerGestures$RootGesture;->b:Landroidx/compose/material3/SheetState;

    return-void
.end method


# virtual methods
.method public final invoke(Landroid/view/MotionEvent;)Lkotlin/u;
    .locals 6

    iget-object v0, p0, Lradiant/MiniPlayerGestures$RootGesture;->b:Landroidx/compose/material3/SheetState;

    invoke-static {v0}, Lradiant/MiniPlayerGestures;->isDragging(Landroidx/compose/material3/SheetState;)Z

    move-result v1

    if-eqz v1, :done

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getActionMasked()I

    move-result v1

    const/4 v2, 0x1

    if-eq v1, v2, :up

    const/4 v2, 0x2

    if-eq v1, v2, :move

    const/4 v2, 0x3

    if-eq v1, v2, :cancel

    goto :done

    # ACTION_MOVE
    :move
    invoke-static {p1}, Lradiant/MiniPlayerGestures;->trackMotion(Landroid/view/MotionEvent;)V

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getRawY()F

    move-result v2

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getEventTime()J

    move-result-wide v4

    invoke-static {v0, v2, v4, v5}, Lradiant/MiniPlayerGestures;->updateDragToYTimed(Landroidx/compose/material3/SheetState;FJ)V

    goto :done

    # ACTION_UP
    :up
    invoke-static {p1}, Lradiant/MiniPlayerGestures;->trackMotion(Landroid/view/MotionEvent;)V

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getRawY()F

    move-result v1

    invoke-virtual {p1}, Landroid/view/MotionEvent;->getEventTime()J

    move-result-wide v4

    invoke-static {v0, v1, v4, v5}, Lradiant/MiniPlayerGestures;->updateDragToYTimed(Landroidx/compose/material3/SheetState;FJ)V

    invoke-static {v0}, Lradiant/MiniPlayerGestures;->releaseVelocity(Landroidx/compose/material3/SheetState;)F

    move-result v1

    iget-object v2, p0, Lradiant/MiniPlayerGestures$RootGesture;->a:Lyl0/l;

    invoke-static {v0, v2, v1}, Lradiant/MiniPlayerGestures;->finishDrag(Landroidx/compose/material3/SheetState;Lyl0/l;F)V

    goto :done

    # ACTION_CANCEL
    :cancel
    iget-object v2, p0, Lradiant/MiniPlayerGestures$RootGesture;->a:Lyl0/l;

    invoke-static {v0, v2}, Lradiant/MiniPlayerGestures;->cancelDrag(Landroidx/compose/material3/SheetState;Lyl0/l;)V

    :done
    sget-object p1, Lkotlin/u;->a:Lkotlin/u;

    return-object p1
.end method

.method public bridge synthetic invoke(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0

    check-cast p1, Landroid/view/MotionEvent;

    invoke-virtual {p0, p1}, Lradiant/MiniPlayerGestures$RootGesture;->invoke(Landroid/view/MotionEvent;)Lkotlin/u;

    move-result-object p1

    return-object p1
.end method
