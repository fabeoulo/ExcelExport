//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2020.07.09 於 11:06:15 AM CST 
//
package com.advantech.webservice.root;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * anonymous complex type 的 Java 類別.
 *
 * <p>
 * 下列綱要片段會指定此類別中包含的預期內容.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="METHOD">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="WIP_ATT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="WIP_NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ITEM_NO" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "method",
    "wipatt",
    "extDept"
})
@XmlRootElement(name = "root")
public class QryWipAttQueryRoot {

    @XmlElement(name = "METHOD", required = true)
    protected QryWipAttQueryRoot.METHOD method;
    @XmlElement(name = "WIP_ATT", required = true)
    protected QryWipAttQueryRoot.WIPATT wipatt;
    @XmlElement(name = "EXT_DEPT", required = true)
    protected String extDept = "PD03";

    public QryWipAttQueryRoot() {
        this.method = new QryWipAttQueryRoot.METHOD();
        this.wipatt = new QryWipAttQueryRoot.WIPATT();
    }

    /**
     * 取得 method 特性的值.
     *
     * @return possible object is {@link Root.METHOD }
     *
     */
    public QryWipAttQueryRoot.METHOD getMETHOD() {
        return method;
    }

    /**
     * 設定 method 特性的值.
     *
     * @param value allowed object is {@link Root.METHOD }
     *
     */
    public void setMETHOD(QryWipAttQueryRoot.METHOD value) {
        this.method = value;
    }

    /**
     * 取得 wipatt 特性的值.
     *
     * @return possible object is {@link Root.WIPATT }
     *
     */
    public QryWipAttQueryRoot.WIPATT getWIPATT() {
        return wipatt;
    }

    /**
     * 設定 wipatt 特性的值.
     *
     * @param value allowed object is {@link Root.WIPATT }
     *
     */
    public void setWIPATT(QryWipAttQueryRoot.WIPATT value) {
        this.wipatt = value;
    }

    public String getExtDept() {
        return extDept;
    }

    public void setExtDept(String extDept) {
        this.extDept = extDept;
    }

    
    /**
     * <p>
     * anonymous complex type 的 Java 類別.
     *
     * <p>
     * 下列綱要片段會指定此類別中包含的預期內容.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class METHOD {

        @XmlAttribute(name = "ID", required = true)
        protected String id = "Advantech.QAM.IPQ.BLL.QryWipAtt001";

        /**
         * 取得 id 特性的值.
         *
         * @return possible object is {@link String }
         *
         */
        public String getID() {
            return id;
        }

        /**
         * 設定 id 特性的值.
         *
         * @param value allowed object is {@link String }
         *
         */
        public void setID(String value) {
            this.id = value;
        }

    }

    /**
     * <p>
     * anonymous complex type 的 Java 類別.
     *
     * <p>
     * 下列綱要片段會指定此類別中包含的預期內容.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="WIP_NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ITEM_NO" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "wipno",
        "itemno"
    })
    public static class WIPATT {

        @XmlElement(name = "WIP_NO", required = true)
        protected String wipno;
        @XmlElement(name = "ITEM_NO", required = true, nillable = true)
        protected Object itemno;

        /**
         * 取得 wipno 特性的值.
         *
         * @return possible object is {@link String }
         *
         */
        public String getWIPNO() {
            return wipno;
        }

        /**
         * 設定 wipno 特性的值.
         *
         * @param value allowed object is {@link String }
         *
         */
        public void setWIPNO(String value) {
            this.wipno = value;
        }

        /**
         * 取得 itemno 特性的值.
         *
         * @return possible object is {@link Object }
         *
         */
        public Object getITEMNO() {
            return itemno;
        }

        /**
         * 設定 itemno 特性的值.
         *
         * @param value allowed object is {@link Object }
         *
         */
        public void setITEMNO(Object value) {
            this.itemno = value;
        }

    }

}
