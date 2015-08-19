package world.parsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import world.ModuleTemplate;
import world.Portal;

import org.xml.sax.Attributes;

public class ModuleTemplateFileParser {

    private static SAXParser m_saxParser;
    private static DefaultHandler m_defaultHandler;
    private static Map<String, ModuleTemplate> m_moduleTemplatesByName = new HashMap<String, ModuleTemplate>();
    private static Set<ModuleTemplate> m_moduleTemplates = new HashSet<ModuleTemplate>();

    private static enum ParseMode {
        READY, MODULE_TYPE_LIST, PORTAL_LIST, PORTAL_RULE
    };

    private static ModuleTemplate m_templateBeingBuilt;
    private static Portal m_portalBeingBuilt;
    private static Map<ModuleTemplate, Integer> m_portalRuleBeingBuilt;
    private static ParseMode m_currentParseMode = ParseMode.READY;

    static {
        try {
            m_saxParser = SAXParserFactory.newInstance().newSAXParser();

            m_defaultHandler = buildDefaultHandler();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public static Set<ModuleTemplate> parseToModuleTemplateSet(String xmlFilename) {
        try {
            m_saxParser.parse(xmlFilename, m_defaultHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return m_moduleTemplates;
    }

    private static void addModuleType(ModuleTemplate type) {
        m_moduleTemplatesByName.put(type.getName(), type);
        m_moduleTemplates.add(type);
    }

    private static DefaultHandler buildDefaultHandler() {
        return new DefaultHandler() {

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                switch(qName) {
                case "moduleTypeList":
                    m_currentParseMode = ParseMode.MODULE_TYPE_LIST;
                    break;
                case "moduleType":
                    if (m_currentParseMode == ParseMode.MODULE_TYPE_LIST) {
                        ModuleTemplateFileParser.addModuleType(new ModuleTemplate(attributes.getValue("name")));
                    } else {
                        System.err.println("Unexpected module type declaration.");
                    }
                    break;
                case "portalList":
                    m_templateBeingBuilt = m_moduleTemplatesByName.get(attributes.getValue("moduleType"));
                    m_currentParseMode = ParseMode.PORTAL_LIST;
                    break;
                case "portal":
                    if (m_currentParseMode == ParseMode.PORTAL_LIST) {
                        m_portalBeingBuilt = new Portal(attributes.getValue("name"));
                        m_portalRuleBeingBuilt = new HashMap<ModuleTemplate, Integer>();
                        m_currentParseMode = ParseMode.PORTAL_RULE;
                    } else {
                        System.err.println("Unexpected portal declaration.");
                    }
                    break;
                case "nextModule":
                    if (m_currentParseMode == ParseMode.PORTAL_RULE) {
                        ModuleTemplate type = m_moduleTemplatesByName.get(attributes.getValue("name"));
                        int weight = Integer.parseInt(attributes.getValue("weight"));
                        m_portalRuleBeingBuilt.put(type, weight);
                    } else {
                        System.err.println("Unexpected next module declaration.");
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                switch(qName) {
                case "moduleTypeList":
                    if (m_currentParseMode == ParseMode.MODULE_TYPE_LIST) {
                        m_currentParseMode = ParseMode.READY;
                    } else {
                        System.err.println("Unexpected end module type list.");
                    }
                    break;
                case "portalList":
                    if (m_currentParseMode == ParseMode.PORTAL_LIST) {
                        m_currentParseMode = ParseMode.READY;
                    } else {
                        System.err.println("Unexpected end portal list.");
                    }
                    break;
                case "portal":
                    if (m_currentParseMode == ParseMode.PORTAL_RULE) {
                        m_templateBeingBuilt.addPortal(m_portalBeingBuilt, m_portalRuleBeingBuilt);
                        m_currentParseMode = ParseMode.PORTAL_LIST;
                    } else {
                        System.err.println("Unexpected end portal rule.");
                    }
                }
            }
        };
    }
}
