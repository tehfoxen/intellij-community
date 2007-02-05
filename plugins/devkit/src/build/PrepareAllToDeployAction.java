/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.devkit.build;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.devkit.DevKitBundle;
import org.jetbrains.idea.devkit.module.PluginModuleType;
import org.jetbrains.idea.devkit.util.ChooseModulesDialog;

import java.util.ArrayList;
import java.util.List;

public class PrepareAllToDeployAction extends PrepareToDeployAction {

  public void actionPerformed(final AnActionEvent e) {
    final Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);

    List<Module> pluginModules = new ArrayList<Module>();
    for (Module aModule : ModuleManager.getInstance(project).getModules()) {
      if (aModule.getModuleType() instanceof PluginModuleType) {
        pluginModules.add(aModule);
      }
    }

    //TODO replace with com.intellij.openapi.roots.ui.configuration.libraryEditor.ChooseModulesDialog
    ChooseModulesDialog dialog = new ChooseModulesDialog(project, pluginModules, DevKitBundle.message("select.plugin.modules.title"),
                                                         DevKitBundle.message("select.plugin.modules.description"));
    dialog.show();
    if (dialog.isOK()) {
      doPrepare(dialog.getSelectedModules());
    }
  }

  public void update(AnActionEvent e) {
    boolean enabled = false;
    final Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
    for (Module aModule : (ModuleManager.getInstance(project).getModules())) {
      if (aModule.getModuleType() instanceof PluginModuleType) {
        enabled = true;
      }
    }
    e.getPresentation().setVisible(enabled);
    e.getPresentation().setEnabled(enabled);
    if (enabled) {
      e.getPresentation().setText(DevKitBundle.message("prepare.for.deployment.all"));
    }
  }
}