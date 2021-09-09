package de.dc.fx.chart.designer.ui.view;

import com.google.inject.Inject;

import de.dc.workbench.fx.core.modul.service.ICommandService;
import de.dc.workbench.fx.core.service.ISelectionService;
import de.dc.workbench.fx.ui.views.ProjectExplorer;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class ChartProjectExplorer extends ProjectExplorer{

	@Inject ISelectionService selectionService;
	@Inject ICommandService commandService;
	
//	private static final String COMMAND_RELOAD = "de.dc.emf.fx.workbench.jmetro.ui.timesheet.app.command.ReloadExplorerCommand";
//	private static final String COMMAND_EXPORT_TIMESHEET = "de.dc.emf.fx.workbench.jmetro.ui.timesheet.app.command.ExportToTimesheetCommand";
	
	@Override
	protected void onTreeViewMouseClicked(MouseEvent event) {
		super.onTreeViewMouseClicked(event);
//		executeMenu.getItems().clear();
//		Object selection = selectionService.getTreeSelection();
//
//		createMenu("Reload Explorer", ()->commandService.execute(COMMAND_RELOAD, null));
//		
//		if (selection instanceof FileResource) {
//			FileResource resource = (FileResource) selection;
//			if (resource.getFile().getName().endsWith("spelltimesheet")) {
//				createMenu("Export To Timesheet", ()->commandService.execute(COMMAND_EXPORT_TIMESHEET, null));
//			}
//		}
	}
	
	public void createMenu(String text, Runnable runnable) {
		MenuItem menuReload = new MenuItem(text);
		menuReload.setOnAction(e-> runnable.run());
		executeMenu.getItems().add(menuReload);
	}
}
