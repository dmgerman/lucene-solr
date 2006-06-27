begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.gdata.server.registry
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|ServiceFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|authentication
operator|.
name|AuthenticationController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
operator|.
name|handler
operator|.
name|RequestHandlerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|StorageController
import|;
end_import

begin_comment
comment|/**  * The enmueration {@link ComponentType} defines the GDATA-Server Components   * available via {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry#lookup(Class, ComponentType)}   * method.  * @see org.apache.lucene.gdata.server.registry.Component  * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry   * @author Simon Willnauer  *  */
end_comment

begin_enum
DECL|enum|ComponentType
specifier|public
enum|enum
name|ComponentType
block|{
comment|/**      * StorageController Type      *       * @see StorageController      */
DECL|enum constant|SuperType
annotation|@
name|SuperType
argument_list|(
name|superType
operator|=
name|StorageController
operator|.
name|class
argument_list|)
DECL|enum constant|STORAGECONTROLLER
name|STORAGECONTROLLER
block|,
comment|/**      * RequestHandlerFactory Type      *       * @see RequestHandlerFactory      */
DECL|enum constant|SuperType
annotation|@
name|SuperType
argument_list|(
name|superType
operator|=
name|RequestHandlerFactory
operator|.
name|class
argument_list|)
DECL|enum constant|REQUESTHANDLERFACTORY
name|REQUESTHANDLERFACTORY
block|,
comment|/**      * INDEXER TYPE      *       */
comment|// TODO not available yet
DECL|enum constant|SuperType
annotation|@
name|SuperType
argument_list|(
name|superType
operator|=
name|Object
operator|.
name|class
argument_list|)
DECL|enum constant|INDEXER
name|INDEXER
block|,
comment|/**      * ServiceFactory Type      *       * @see ServiceFactory      */
DECL|enum constant|SuperType
annotation|@
name|SuperType
argument_list|(
name|superType
operator|=
name|ServiceFactory
operator|.
name|class
argument_list|)
DECL|enum constant|SERVICEFACTORY
name|SERVICEFACTORY
block|,
comment|/**      * Supertype for AuthenticationController implementations      * @see AuthenticationController      */
DECL|enum constant|SuperType
annotation|@
name|SuperType
argument_list|(
name|superType
operator|=
name|AuthenticationController
operator|.
name|class
argument_list|)
DECL|enum constant|AUTHENTICATIONCONTROLLER
name|AUTHENTICATIONCONTROLLER
block|}
end_enum

end_unit

