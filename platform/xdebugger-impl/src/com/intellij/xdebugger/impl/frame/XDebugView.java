// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.xdebugger.impl.frame;

import com.intellij.execution.ui.layout.ViewContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.SingleAlarm;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.EventObject;

/**
 * @author nik
 */
public abstract class XDebugView implements Disposable {
  public enum SessionEvent {PAUSED, BEFORE_RESUME, RESUMED, STOPPED, FRAME_CHANGED, SETTINGS_CHANGED}

  private final SingleAlarm myClearAlarm;
  private static final int VIEW_CLEAR_DELAY = 100; //ms

  public XDebugView() {
    myClearAlarm = new SingleAlarm(() -> clear(), VIEW_CLEAR_DELAY, this);
  }

  protected final void requestClear() {
    myClearAlarm.cancelAndRequest();
  }

  protected final void cancelClear() {
    myClearAlarm.cancel();
  }

  protected abstract void clear();

  public abstract void processSessionEvent(@NotNull SessionEvent event, @NotNull XDebugSession session);

  @Nullable
  protected static XDebugSession getSession(@NotNull EventObject e) {
    Component component = e.getSource() instanceof Component ? (Component)e.getSource() : null;
    return component == null ? null : getSession(component);
  }

  @Nullable
  public static XDebugSession getSession(@NotNull Component component) {
    return getData(XDebugSession.DATA_KEY, component);
  }

  @Nullable
  protected VirtualFile getCurrentFile(@NotNull Component component) {
    XDebugSession session = getSession(component);
    if (session != null) {
      XSourcePosition position = session.getCurrentPosition();
      if (position != null) {
        return position.getFile();
      }
    }
    return null;
  }


  @Nullable
  public static <T> T getData(DataKey<T> key, @NotNull Component component) {
    DataContext dataContext = DataManager.getInstance().getDataContext(component);
    ViewContext viewContext = ViewContext.CONTEXT_KEY.getData(dataContext);
    ContentManager contentManager = viewContext == null ? null : viewContext.getContentManager();
    if (contentManager != null) {
      T data = key.getData(DataManager.getInstance().getDataContext(contentManager.getComponent()));
      if (data != null) {
        return data;
      }
    }
    return key.getData(dataContext);
  }
}
