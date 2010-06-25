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
package org.kathrynhuxtable.eclipse.plugin.setmacdocument;

import org.eclipse.core.resources.IFile;

import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSView;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Cocoa-specific code.
 *
 * <p>Set the represented filename associated with the active editor, or remove
 * it if no file is associated, or there is no active editor.</p>
 *
 * @author Kathryn Huxtable
 */
@SuppressWarnings("restriction")
public class CocoaCode {

    /**
     * Set the represented filename for the Cocoa NSWindow associated with the
     * active editor to the filename in that editor, or set it to empty string
     * if no file is associated.
     *
     * @param site the workbench part's site.
     */
    public void setRepresentedFilename(IWorkbenchPartSite site) {
        Shell shell = site.getShell();

        if (shell != null) {
            NSView view = shell.view;

            if (view != null) {
                NSWindow w = view.window();

                if (w != null) {
                    // Create the Objective C selector for the method to set the filename,
                    // and get the filename.
                    long     sel     = OS.sel_registerName("setRepresentedFilename:");
                    NSString fileStr = getFilename(site);

                    // Set the represented filename.
                    OS.objc_msgSend(w.id, sel, fileStr.id);
                }
            }
        }
    }

    /**
     * Retrieve the filename associated with the current workbench part.
     *
     * @param  site the workbench part's site.
     *
     * @return an NSString representing the filename, or an empty NSString if no
     *         filename is associated.
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
}
