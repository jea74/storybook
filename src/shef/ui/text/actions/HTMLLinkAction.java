/*
 * Created on Feb 26, 2005
 *
 */
package shef.ui.text.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;

import shef.ui.ShefUtils;
import shef.ui.text.HTMLUtils;
import shef.ui.text.dialogs.HyperlinkDialog;
import storybook.i18n.I18N;

/**
 * Action which displays a dialog to insert a hyperlink
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLLinkAction extends HTMLTextEditAction {

	private static final long serialVersionUID = 1L;

	public HTMLLinkAction() {
		super(I18N.getMsg("shef.hyperlink_"));
		putValue(MNEMONIC_KEY, (int) I18N.getMnemonic("shef.hyperlink_"));
		putValue(SMALL_ICON, ShefUtils.getIconX16("link"));
		putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
	}

	@Override
	protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
		HyperlinkDialog dlg = createDialog(editor);
		if (dlg == null) {
			return;
		}

		dlg.setLocationRelativeTo(dlg.getParent());
		dlg.setLinkText(editor.getSelectedText());
		dlg.setVisible(true);
		if (dlg.hasUserCancelled()) {
			return;
		}

		editor.requestFocusInWindow();
		editor.replaceSelection(dlg.getHTML());
	}

	@Override
	protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
		HyperlinkDialog dlg = createDialog(editor);
		if (dlg == null) {
			return;
		}

		if (editor.getSelectedText() != null) {
			dlg.setLinkText(editor.getSelectedText());
		}
		dlg.setLocationRelativeTo(dlg.getParent());
		dlg.setVisible(true);
		if (dlg.hasUserCancelled()) {
			return;
		}

		String tagText = dlg.getHTML();
		if (editor.getSelectedText() == null) {
			tagText += "&nbsp;";
		}

		editor.replaceSelection("");
		HTMLUtils.insertHTML(tagText, HTML.Tag.A, editor);
	}

	protected HyperlinkDialog createDialog(JTextComponent ed) {
		Window w = SwingUtilities.getWindowAncestor(ed);
		HyperlinkDialog d = null;
		if (w != null && w instanceof Frame) {
			d = new HyperlinkDialog((Frame) w);
		} else if (w != null && w instanceof Dialog) {
			d = new HyperlinkDialog((Dialog) w);
		}

		return d;
	}
}
