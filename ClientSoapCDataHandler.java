package com.jaxws.ext.handlers;

import java.io.IOException;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ClientSoapCDataHandler implements SOAPHandler<SOAPMessageContext> {
  public String xpathExpression;

  public ClientSoapCDataHandler(String xpath) {
    xpathExpression = xpath;
  }

  public String getXpathExpression() {
    return xpathExpression;
  }

  public void setXpathExpression(String xpath) {
    xpathExpression = xpath;
  }

  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

    if(isRequest) {

      try {
        SOAPMessage soapMsg = context.getMessage();
        SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
        SOAPHeader soapHeader = soapEnv.getHeader();
        SOAPBody soapBody = soapEnv.getBody();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xPath = xpathFactory.newXPath();

        NodeList requestNodes = (NodeList) xPath.evaluate(xpathExpression, soapBody, XPathConstants.NODESET); 
        for(int i = 0; i < requestNodes.getLength(); i++) {
          Node requestNode = requestNodes.item(i).getFirstChild();
          CDATASection cdataNode = requestNode.getOwnerDocument().createCDATASection(requestNode.getNodeValue());
          requestNode.getParentNode().replaceChild(cdataNode, requestNode);
        }
      } catch (SOAPException e) {
        System.err.println(e);
        e.printStackTrace();
      } catch (DOMException e) {
        System.err.println(e);
        e.printStackTrace();
      } catch (XPathExpressionException e) {
        System.err.println(e);
        e.printStackTrace();
      }
    }

    return true;
  }

  @Override
  public boolean handleFault(SOAPMessageContext context) {
    return true;
  }

  @Override
  public void close(MessageContext context) {}

  @Override
  public Set<QName> getHeaders() {
    return null;
  }
}
