package io.vertx.mvc.views;

import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.TemplateHandler;
import io.vertx.ext.apex.templ.HandlebarsTemplateEngine;
import io.vertx.ext.apex.templ.JadeTemplateEngine;
import io.vertx.ext.apex.templ.MVELTemplateEngine;
import io.vertx.ext.apex.templ.TemplateEngine;
import io.vertx.ext.apex.templ.ThymeleafTemplateEngine;
import io.vertx.mvc.Config;

public class TemplateEngineManager implements TemplateHandler {

	private TemplateEngine defaultEngine;

	private HandlebarsTemplateEngine hbsEngine;
	private MVELTemplateEngine mvelEngine;
	private JadeTemplateEngine jadeEngine;
	private ThymeleafTemplateEngine thymeleafEngine;
	private String tplDir;

	public TemplateEngineManager(Config config) {
		this.tplDir = config.tplDir;
		if (!this.tplDir.endsWith("/")) {
			this.tplDir += "/";
		}
		hbsEngine = HandlebarsTemplateEngine.create();
		mvelEngine = MVELTemplateEngine.create();
		jadeEngine = JadeTemplateEngine.create();
		thymeleafEngine = ThymeleafTemplateEngine.create();
		defaultEngine = thymeleafEngine;
	}



	public TemplateEngine fromViewName(String tplName) {
		int pointIdx = tplName.lastIndexOf(".");
		if (pointIdx == -1) {
			return defaultEngine;
		}
		String extension = tplName.substring(pointIdx + 1, tplName.length());
		switch(extension) {
			case "hbs":
				return hbsEngine;
			case "templ":
				return mvelEngine;
			case "jade":
				return jadeEngine;
			case "html":
				return thymeleafEngine;
		}
		return defaultEngine;
	}

	@Override
	public void handle(RoutingContext context) {
		String tplName = (String)context.data().get("tplName");
		TemplateEngine engine = fromViewName(tplName);
		tplName = tplDir + tplName;
		System.out.println("Render view : "+tplName);
		engine.render(context, tplName, res -> {
			if (res.succeeded()) {
				context.response().putHeader("Content-Type", "text/html").end(res.result());
			} else {
				context.fail(res.cause());
			}
		});
	}
}