package jrds.factories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;
import jrds.starter.Starter;
import jrds.webapp.Discover.ProbeDescSummary;
import jrds.webapp.DiscoverAgent;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProbeMeta {
    public class EmptyDiscoverAgent extends DiscoverAgent {
        public EmptyDiscoverAgent() {
            super("Empty");
        }

        @Override
        public List<FieldInfo> getFields() {
            return Collections.emptyList();
        }

        /**
         * This discover agent does nothing
         *
         */
        @Override
        public void doHtmlDiscoverFields(JrdsDocument document) {
        }

        @Override
        public boolean exist(String hostname, HttpServletRequest request) {
            return false;
        }

        @Override
        public void addConnection(JrdsElement hostElement,
                HttpServletRequest request) {
        }

        @Override
        public boolean isGoodProbeDesc(ProbeDescSummary summary) {
            return false;
        }

        @Override
        public void addProbe(JrdsElement hostElement,
                ProbeDescSummary summary, HttpServletRequest request) {
            return;
        }
    };

    /**
     * @author bacchell
     *
     */
    public class EmptyStarter extends Starter {
    };

    Class<? extends DiscoverAgent> discoverAgent() default EmptyDiscoverAgent.class;
    Class<? extends Starter> timerStarter() default EmptyStarter.class;
    Class<? extends Starter> topStarter() default EmptyStarter.class;

}
