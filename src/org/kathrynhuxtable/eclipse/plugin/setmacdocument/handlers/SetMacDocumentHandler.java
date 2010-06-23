package org.kathrynhuxtable.eclipse.plugin.setmacdocument.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;

import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSView;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
@SuppressWarnings("restriction")
public class SetMacDocumentHandler extends AbstractHandler {

    /**
     * The constructor.
     */
    public SetMacDocumentHandler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     *
     * @param  event DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ExecutionException DOCUMENT ME!
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window   = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        Shell            shell    = window.getShell();
        IEditorPart      editor   = HandlerUtil.getActiveEditorChecked(event);
        IFile            file     = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        String           filename = file == null ? "" : file.getLocation().toOSString();

        NSView view = shell.view;

        if (view != null) {
            NSWindow w = view.window();

            if (w != null) {
                long     sel = OS.sel_registerName("setRepresentedFilename:");
                NSString str = org.eclipse.swt.internal.cocoa.NSString.stringWith(filename);

                OS.objc_msgSend(w.id, sel, str.id);
            }
        }

        return null;
    }
}
