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
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
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
        IWorkbenchPartSite site    = part.getSite();
        NSString           fileStr = getFilename(site);
        Shell              shell   = site.getShell();

        if (shell != null) {
            NSView view = shell.view;

            if (view != null) {
                NSWindow w = view.window();

                if (w != null) {
                    long sel = OS.sel_registerName("setRepresentedFilename:");

                    OS.objc_msgSend(w.id, sel, fileStr.id);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  site DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private NSString getFilename(IWorkbenchPartSite site) {
        String filename = "";

        if (site != null && site.getPage() != null) {
            IEditorPart editor = site.getPage().getActiveEditor();

            if (editor != null && editor.getEditorInput() != null) {
                IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);

                if (file != null) {
                    filename = file.getLocation().toOSString();
                }
            }
        }

        return org.eclipse.swt.internal.cocoa.NSString.stringWith(filename);
    }

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        IWorkbench workbench = PlatformUI.getWorkbench();

        workbench.addWindowListener(new MyWindowListener());

        for (IWorkbenchWindow w : workbench.getWorkbenchWindows()) {
            addPartListeners(w);
        }

        System.out.println("Got here");
    }

    /**
     * DOCUMENT ME!
     *
     * @param w
     */
    private void addPartListeners(IWorkbenchWindow w) {
        IPartService service = w.getPartService();

        if (service != null) {
            service.addPartListener(new MyPartListener());
        }
    }

    /**
     * DOCUMENT ME!
     */
    private final class MyWindowListener implements IWindowListener {

        /**
         * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
         */
        @Override
        public void windowActivated(IWorkbenchWindow w) {
            System.out.println("workbenth window activated.");
        }

        /**
         * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
         */
        @Override
        public void windowClosed(IWorkbenchWindow w) {
        }

        /**
         * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
         */
        @Override
        public void windowDeactivated(IWorkbenchWindow w) {
        }

        /**
         * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
         */
        @Override
        public void windowOpened(IWorkbenchWindow w) {
            addPartListeners(w);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private final class MyPartListener implements IPartListener2 {

        /**
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partActivated(IWorkbenchPartReference ref) {
            System.out.println("Part activated: " + ref.getTitle());
            setRepresentedFilename(ref.getPart(false));
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partBroughtToTop(IWorkbenchPartReference ref) {
            System.out.println("Part brought to top: " + ref.getTitle());
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partClosed(IWorkbenchPartReference ref) {
            System.out.println("Part closed: " + ref.getTitle());
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partDeactivated(IWorkbenchPartReference ref) {
            System.out.println("Part deactivated: " + ref.getTitle());
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partHidden(IWorkbenchPartReference ref) {
            System.out.println("Part hidden: " + ref.getTitle());
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partInputChanged(IWorkbenchPartReference ref) {
            System.out.println("Part input changed: " + ref.getTitle());
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partOpened(IWorkbenchPartReference ref) {
            System.out.println("Part opened: " + ref.getTitle());
        }

        /**
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
         */
        @Override
        public void partVisible(IWorkbenchPartReference ref) {
            System.out.println("Part visible: " + ref.getTitle());
        }
    }
}
