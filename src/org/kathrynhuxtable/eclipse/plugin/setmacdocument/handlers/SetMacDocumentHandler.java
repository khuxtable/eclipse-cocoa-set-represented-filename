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

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSView;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

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
     * @param site DOCUMENT ME!
     * @param part DOCUMENT ME!
     */
    private void setRepresentedFilename(IWorkbenchPartSite site, IEditorPart part) {
        Shell shell = site.getShell();

        if (shell != null) {
            NSView view = shell.view;

            if (view != null) {
                NSWindow w = view.window();

                if (w != null) {
                    long     sel     = OS.sel_registerName("setRepresentedFilename:");
                    NSString fileStr = getFilename(site);

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

        IEvaluationService service = (IEvaluationService) workbench.getService(IEvaluationService.class);

        service.addEvaluationListener(new MyExpression(), new MyPropertyChangeListener(service), "org.kathrynhuxtable.activeEditorChange");

        System.out.println("Got here");
    }

    /**
     * DOCUMENT ME!
     */
    private final class MyExpression extends Expression {
        boolean rc = false;

        /**
         * @see org.eclipse.core.expressions.Expression#collectExpressionInfo(org.eclipse.core.expressions.ExpressionInfo)
         */
        @Override
        public void collectExpressionInfo(ExpressionInfo info) {
            info.addVariableNameAccess(ISources.ACTIVE_EDITOR_NAME);
        }

        /**
         * @see org.eclipse.core.expressions.Expression#evaluate(org.eclipse.core.expressions.IEvaluationContext)
         */
        @Override
        public EvaluationResult evaluate(IEvaluationContext context) throws CoreException {
            rc = !rc;
            return EvaluationResult.valueOf(rc);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author  $author$
     * @version $Revision$, $Date$
     */
    private final class MyPropertyChangeListener implements IPropertyChangeListener {

        IEvaluationService service;

        /**
         * Creates a new MyPropertyChangeListener object.
         *
         * @param service DOCUMENT ME!
         */
        MyPropertyChangeListener(IEvaluationService service) {
            this.service = service;
        }

        /**
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent arg0) {
            Object editor = service.getCurrentState().getVariable(ISources.ACTIVE_EDITOR_NAME);
            Object part   = service.getCurrentState().getVariable(ISources.ACTIVE_PART_NAME);

            if (part != null && part instanceof IWorkbenchPart) {
                IWorkbenchPartSite site = ((IWorkbenchPart) part).getSite();

                setRepresentedFilename(site, editor instanceof IEditorPart ? (IEditorPart) editor : null);
            }
        }

    }
}
