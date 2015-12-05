package org.mycat.web.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/csvAction")
public class CsvAction {

	@RequestMapping("/excelCSV")
	public void exportExcel(HttpServletResponse response,
			HttpServletRequest request) {
		String fileName = request.getParameter("fileName");
		if (fileName == null || fileName.isEmpty()) {
			fileName = "default";
		}
		response.reset();
		response.setContentType("application/csv");
		response.addHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\".csv");

		java.io.OutputStream outp = null;

		try {
			outp = response.getOutputStream();
			String serviceName = request.getParameter("service");
			String methodName = request.getParameter("method");
			RainbowContext context = new RainbowContext(serviceName, methodName);
			context.addAttr("ds", request.getParameter("ds"));
			SoaManager.getInstance().invoke(context);
			int filelength = 0;
			if (context.isSuccess()) {

				List<Map<String, Object>> dataList = context.getRows();
				for (Map<String, Object> data : dataList) {
					String values = data.values().toString();
					outp.write(values.getBytes());
					filelength += values.length();
				}
				response.setHeader("Content_length",
						new Integer(filelength).toString());

			} else {
				outp.write(context.getMsg().getBytes());
			}
		} catch (Exception ex) {

		} finally {
			if (outp != null) {
				try {
					outp.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
