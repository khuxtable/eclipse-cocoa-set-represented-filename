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
package org.kathrynhuxtable.eclipse.plugin.cocoasetrepresentedfilename;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.ui.ISources;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

/**
 * Startup class.
 *
 * <p>This runs at startup and registers a listener on changes to active
 * editor.</p>
 *
 * @author Kathryn Huxtable
 */
public class Startup implements IStartup {

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    public void earlyStartup() {
        // Get the evaluation service.
        IEvaluationService service = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);

        // Register a listener on active editor name property changes.
        service.addEvaluationListener(new ActiveEditorChangeExpression(),
                                      new ActiveEditorPropertyChangeListener(service),
                                      ISources.ACTIVE_EDITOR_NAME);
    }

    /**
     * Detect changes in active editor name.
     */
    private static final class ActiveEditorChangeExpression extends Expression {

        private boolean rc = false;

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
     * Property change listener for changes to active editor name.
     */
    private static final class ActiveEditorPropertyChangeListener implements IPropertyChangeListener {

        private IEvaluationService service;
        private CocoaCode          representedFilename;

        /**
         * Creates a new ActiveEditorPropertyChangeListener object.
         *
         * @param service the evaluation service we're registered with.
         */
        ActiveEditorPropertyChangeListener(IEvaluationService service) {
            // Save the service.
            this.service             = service;
            
            // Instantiate the class to do the work.
            this.representedFilename = new CocoaCode();
        }

        /**
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event) {
            Object part = service.getCurrentState().getVariable(ISources.ACTIVE_PART_NAME);

            // If the active part is an IWorkbenchPart, get its site and set the filename, if any.
            if (part != null && part instanceof IWorkbenchPart) {
                representedFilename.setRepresentedFilename(((IWorkbenchPart) part).getSite());
            }
        }
    }
}
