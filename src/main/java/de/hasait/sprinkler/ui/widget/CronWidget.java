package de.hasait.sprinkler.ui.widget;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.hasait.sprinkler.ui.UiConstants;
import de.hasait.sprinkler.util.Util;

import java.time.LocalDateTime;

public class CronWidget {

    private final TextField cronExpressionField = new TextField(UiConstants.CAPTION_CRON_EXPRESSION);
    private final TextField next1PreviewLabel = new TextField(UiConstants.CAPTION_CRON_NEXT);
    private final TextField nextRelativePreviewLabel = new TextField(UiConstants.CAPTION_CRON_NEXT_RELATIVE);
    private final TextField next2PreviewLabel = new TextField(UiConstants.CAPTION_CRON_NEXT_NEXT);

    public TextField getCronExpressionField() {
        return cronExpressionField;
    }

    public TextField getNext1PreviewLabel() {
        return next1PreviewLabel;
    }

    public TextField getNextRelativePreviewLabel() {
        return nextRelativePreviewLabel;
    }

    public TextField getNext2PreviewLabel() {
        return next2PreviewLabel;
    }

    public void populateLayout(HasComponents hasComponents) {
        cronExpressionField.setTooltipText(UiConstants.TOOLTOP_CRON_EXPRESSION);
        hasComponents.add(cronExpressionField);

        next1PreviewLabel.setReadOnly(true);
        hasComponents.add(next1PreviewLabel);

        nextRelativePreviewLabel.setReadOnly(true);
        hasComponents.add(nextRelativePreviewLabel);

        next2PreviewLabel.setReadOnly(true);
        hasComponents.add(next2PreviewLabel);
    }

    public void populateBinder(Binder<?> binder) {
        binder.forMemberField(cronExpressionField) //
                .withValidator(value -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime next1 = Util.determineNext(value, now);
                    next1PreviewLabel.setValue(UiConstants.formatNext(next1));
                    nextRelativePreviewLabel.setValue(Util.determineNextRelative(now, next1, Integer.MAX_VALUE));
                    LocalDateTime next2 = Util.determineNext(value, next1);
                    next2PreviewLabel.setValue(UiConstants.formatNext(next2));
                    return true;
                }, "Invalid");
    }
}
