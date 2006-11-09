begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|TYPE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|RUNTIME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * The {@link Component} annotation is used to annotate a class as a  * server-component of the GDATA-Server. Annotated class can be configured via  * the gdata-config.xml file to be looked up by aribaty classes at runtime via  * the  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry#lookup(Class, ComponentType)}  * method.  *<p>  * Classes annotated with the Component annotation need to provide a default  * constructor to be instanciated via reflection. Components of the GData-Server  * are definded in the  * {@link org.apache.lucene.gdata.server.registry.ComponentType} enumeration.  * Each of the enum types are annotated with a  * {@link org.apache.lucene.gdata.server.registry.SuperType} annotation. This  * annotation specifies the super class or interface of the component. A class  * annotated with the Component annotation must implement or extends this  * defined super-type. This enables developers to use custom implemetations of  * the component like a custom {@link org.apache.lucene.gdata.storage.Storage}.  *</p>  *<p>  * Each ComponentType can only registerd once as the  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry} does not  * provide multipe instances of a ComponentType.  *</p>  *<p>  * This annotation can only annotate types and can be accessed at runtime.  * {@link java.lang.annotation.Target} ==  * {@link java.lang.annotation.ElementType#TYPE} and  * {@link java.lang.annotation.Retention} ==  * {@link java.lang.annotation.RetentionPolicy#RUNTIME}.  *   * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry  * @see org.apache.lucene.gdata.server.registry.ComponentType  * @see org.apache.lucene.gdata.server.registry.SuperType  *   *   * @author Simon Willnauer  *   */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
block|{
name|TYPE
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|value
operator|=
name|RUNTIME
argument_list|)
DECL|interface|Component
specifier|public
annotation_defn|@interface
name|Component
block|{
comment|/**      * @see ComponentType      * @return - the component type      */
DECL|method|componentType
name|ComponentType
name|componentType
parameter_list|()
function_decl|;
block|}
end_annotation_defn

end_unit

