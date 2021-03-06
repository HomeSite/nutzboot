package org.nutz.boot.starter.nutz.mvc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutFilter;

@IocBean
public class NutFilterStarter implements WebFilterFace {
	
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

    public String getName() {
        return "nutz";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    @IocBean(name="nutFilter")
    public NutFilter createNutFilter() {
    	return new NutFilter();
    }
    
    public Filter getFilter() {
        return ioc.get(NutFilter.class, "nutFilter");
    }

    public Map<String, String> getInitParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("modules", NbMainModule.class.getName());
        if (conf.has("nutz.mvc.ignore")) {
        	params.put("ignore", conf.get("nutz.mvc.ignore"));
        }
        StringBuilder sb = new StringBuilder();
        for (WebServletFace face : appContext.getBeans(WebServletFace.class)) {
            sb.append(',').append(face.getPathSpec());
        }
        params.put("exclusions", conf.get("nutz.mvc.exclusions", "") + sb);
        return params;
    }

    public int getOrder() {
        return conf.getInt("web.filter.order.nutz", FilterOrder.NutFilter);
    }
}
