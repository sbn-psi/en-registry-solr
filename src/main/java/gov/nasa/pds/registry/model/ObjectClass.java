package gov.nasa.pds.registry.model;

/**
 * @author pramirez
 *
 */
public enum ObjectClass {
  ASSOCIATION("Association", Association.class),
  AUDITABLE_EVENT("AuditableEvent", AuditableEvent.class),
  CLASSIFICATION("Classification", Classification.class),
  CLASSIFICATION_NODE("ClassificationNode", ClassificationNode.class),
  CLASSIFICATION_SCHEME("ClassificationScheme", ClassificationScheme.class),
  EXTERNAL_IDENTIFIER("ExternalIdentifier", ExternalIdentifier.class),
  EXTERNAL_LINK("ExternalLink", ExternalLink.class),
  EXTRINSIC_OBJECT("ExtrinsicObject", ExtrinsicObject.class),
  REGISTRY_PACKAGE("RegistryPackage", RegistryPackage.class),
  SERVICE("Service", Service.class),
  SERVICE_BINDING("ServiceBinding", ServiceBinding.class),
  SPECIFICATION_LINK("SpecificationLink", SpecificationLink.class);
  
  private String name;
  private Class<? extends RegistryObject> clazz;
  
  ObjectClass(String name, Class<? extends RegistryObject> clazz) {
    this.name = name;
    this.clazz = clazz;
  }
  
  public static ObjectClass fromName(String name) {
    if (name != null) {
      for (ObjectClass objectClass : ObjectClass.values()) {
        if (name.equalsIgnoreCase(objectClass.name)) {
          return objectClass;
        }
      }
    }
    return null;
  }

  public String getName() {
    return name;
  }
  
  public Class<? extends RegistryObject> getObjectClass() {
    return clazz;
  }
}
