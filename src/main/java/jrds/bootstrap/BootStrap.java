package jrds.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class BootStrap {
	static final private String[] propertiesList = { "jetty.port", "propertiesFile", "loglevel"};
	static final private String defaultCommand = "jetty";
	static final private Map<String, String> cmdClasses = new HashMap<String, String>();
	static {
		cmdClasses.put("jetty", "jrds.standalone.Jetty");
		cmdClasses.put("wikidoc", "jrds.standalone.EnumerateWikiProbes");
		cmdClasses.put("checkjar", "jrds.standalone.CheckJar");
		cmdClasses.put("collect", "jrds.standalone.Collector");
		cmdClasses.put("dosnmpprobe", "jrds.standalone.DoSnmpProbe");
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {

		File baseClassPath = getBaseClassPath();
		Properties configuration = new Properties();
		for(String prop: propertiesList) {
			String propVal = System.getProperty(prop);
			if(propVal != null)
				configuration.put(prop, propVal);
		}

		//To remove WEB-INF/lib in the path and find the web root
		File webRoot = baseClassPath.getParentFile().getParentFile();
		if(webRoot.isDirectory()) {
			configuration.put("webRoot", webRoot.getAbsolutePath());
		}

		ClassLoader cl = makeClassLoader(baseClassPath);

		String commandName = defaultCommand;
		if(args.length > 0) {
			commandName = args[0].trim().toLowerCase();
		}

		boolean help = false;
		if("help".equals(commandName)) {
			help = true;
			if(args.length > 1)
				commandName = args[1].trim().toLowerCase();
			else
				doHelp(null);
		}
		else {
			if(args.length > 1)
				args = Arrays.copyOfRange(args, 1, args.length);
			else
				args = new String[0];
		}

		CommandStarter command = findCmdClass(commandName, cl);
		if(command == null) {
			System.exit(0);
		}
		if(help) {
			command.help();
			System.exit(0);
		}
		command.configure(configuration);
		try {
			command.start(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private String ressourcePath(Object o) {
		if(o instanceof Class<?>) {
			Class<?> c = (Class<?>) o;
			return "/".concat(c.getName().replace(".", "/").concat(".class"));
		}
		else if(o instanceof String) {
			return (String) o;
		}
		return "";
	}

	static private File getBaseClassPath() {
		try {
			String path = ressourcePath(BootStrap.class);
			URL me = BootStrap.class.getResource(path);
			String protocol = me.getProtocol();
			URL rootUrl = null;
			if("jar".equals(protocol)) {
				JarURLConnection cnx = (JarURLConnection) me.openConnection();
				rootUrl = cnx.getJarFileURL();
			}
			else if("file".equals(protocol)) {
				rootUrl = me;
			}
			else
			    return null;

			File file = new File(rootUrl.getFile());
			File baseClassPath = file.getParentFile();

			return baseClassPath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static private ClassLoader makeClassLoader(File baseClassPath) {
		try {
			List<URL> classPath= new ArrayList<URL>();

			classPath.add(baseClassPath.toURI().toURL());

			FileFilter jarfilter = new  FileFilter(){
				public boolean accept(File file) {
					return file.isFile() && file.getName().endsWith(".jar");
				}
			};
			for(File f: baseClassPath.listFiles(jarfilter)) {
				classPath.add(f.toURI().toURL());
			}

			String libspath = System.getProperty("libspath","");

			for(String path: libspath.split(String.valueOf(File.pathSeparatorChar))) {
				File libFile = new File(path);
				if(libFile.isDirectory()) {
					for(File f: libFile.listFiles(jarfilter)) {
						classPath.add(f.toURI().toURL());
					}
				}
				else if(libFile.isFile() && libFile.getName().endsWith(".jar"))
					classPath.add(libFile.toURI().toURL());
			}

			return URLClassLoader.newInstance(classPath.toArray(new URL[classPath.size()]));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	static private void doHelp(CommandStarter command) {
		if(command == null) {
			System.out.println("Lists of available command:");
			for(String commandName: cmdClasses.keySet()) {
				System.out.println("    " + commandName);
			}
			System.out.println("");
			System.out.println("Lists of configuration propreties:");
			for(String propName: propertiesList) {
				System.out.println("    " + propName);
			}
			System.out.println(String.format("A class path can be auto build with the propery libspath, a list of directory or jar, separated by a %s", File.pathSeparatorChar));

		}
		System.exit(0);
	}

	static private CommandStarter findCmdClass(String command, ClassLoader cl) {
		try {
			String cmdClass = cmdClasses.get(command);
			if(cmdClass == null) {
				System.out.println("Unknown command: " + command);
				return null;
			}
			Class<?>  jettyStarter = cl.loadClass(cmdClass);
			CommandStarter cmd = (CommandStarter) jettyStarter.newInstance();
			return cmd;
		} catch (ClassNotFoundException e1) {
			System.err.println("JRDS installation not found");
			System.exit(1);
		} catch (IllegalArgumentException e) {
			e.getCause().printStackTrace();
		} catch (InstantiationException e) {
			e.getCause().printStackTrace();
		} catch (IllegalAccessException e) {
			e.getCause().printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
