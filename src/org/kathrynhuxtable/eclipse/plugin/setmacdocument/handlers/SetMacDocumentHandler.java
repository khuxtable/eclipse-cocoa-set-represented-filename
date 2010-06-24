/*
 * Copyright (c) 2010 Kathryn Huxtable
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 *
 *   http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kathrynhuxtable.eclipse.plugin.setmacdocument.handlers;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSView;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
@SuppressWarnings("restriction")
public class SetMacDocumentHandler implements IStartup {

    /**
     * The constructor.
     */
    public SetMacDocumentHandler() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param part DOCUMENT ME!
     */
    private void setRepresentedFilename(IWorkbenchPart part) {
        IWorkbenchPartSite site     = part.getSite();
        IEditorPart        editor   = site.getPage().getActiveEditor();
        IFile              file     = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        String             filename = file == null ? null : file.getLocation().toOSString();
        Shell              shell    = site.getShell();
        NSView             view     = shell.view;

        if (view != null) {
            NSWindow w = view.window();

            if (w != null) {
                long     sel = OS.sel_registerName("setRepresentedFilename:");
                NSString str = filename == null ? null : org.eclipse.swt.internal.cocoa.NSString.stringWith(filename);

                OS.objc_msgSend(w.id, sel, str.id);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        
        for (IWorkbenchWindow w : workbench.getWorkbenchWindows()) {
            IPartService service = w.getPartService();

            if (service != null) {
                service.addPartListener(new IPartListener() {

                        @Override
                        public void partActivated(IWorkbenchPart part) {
                            System.out.println("Part activated: " + part.getTitle());
                            setRepresentedFilename(part);
                        }

                        @Override
                        public void partBroughtToTop(IWorkbenchPart part) {
                            System.out.println("Part brought to top: " + part.getTitle());
                        }

                        @Override
                        public void partClosed(IWorkbenchPart part) {
                            System.out.println("Part closed: " + part.getTitle());
                        }

                        @Override
                        public void partDeactivated(IWorkbenchPart part) {
                            System.out.println("Part deactivated: " + part.getTitle());
                        }

                        @Override
                        public void partOpened(IWorkbenchPart part) {
                            System.out.println("Part opened: " + part.getTitle());
                        }
                    });
            }
        }

        System.out.println("Got here");
    }
}
