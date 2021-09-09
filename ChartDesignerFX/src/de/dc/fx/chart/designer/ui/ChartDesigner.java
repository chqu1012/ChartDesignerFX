package de.dc.fx.chart.designer.ui;

import com.google.inject.Inject;

import de.dc.workbench.fx.core.workspace.di.WorkspaceModule;
import de.dc.workbench.fx.core.xtext.IXtextService;
import de.dc.workbench.fx.core.xtext.di.XtextModule;
import de.dc.workbench.fx.ui.EmfApplicationStandalone;
import de.dc.workbench.fx.ui.EmfWorkbench;
import de.dc.workbench.fx.ui.chart.spell.ChartDslStandaloneSetup;
import de.dc.workbench.fx.ui.monaco.di.MonacoModule;
import de.dc.workbench.fx.ui.monaco.service.IMonacoLanguageService;
import de.dc.workbench.fx.ui.views.di.ViewsModule;

public class ChartDesigner extends EmfApplicationStandalone{

	private static final String ID = "chart";
	
	@Inject IXtextService xtextService;
	@Inject IMonacoLanguageService monacoService;
	
	@Override
	protected void addModules() {
		addModule(new ViewsModule());
		addModule(new WorkspaceModule());
		addModule(new MonacoModule());
		addModule(new XtextModule());
	}
	
	@Override
	protected void initStandaloneWorkbench(EmfWorkbench workbench) {
		super.initStandaloneWorkbench(workbench);
		
		ChartDslStandaloneSetup setup = new ChartDslStandaloneSetup();
		xtextService.register(ID, setup);
		xtextService.registerByFileExtension("spellchart", setup);
		
		monacoService.loadAll();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
