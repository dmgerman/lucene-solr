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
name|FIELD
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
comment|/**  * This Annotation is use to annotate  * {@link org.apache.lucene.gdata.server.registry.ComponentType} elements to  * specify an interface e.g. super type of a defined component.  *<p>This annotation will be visible at runtime</p>  * @see org.apache.lucene.gdata.server.registry.Component  * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry  *   * @author Simon Willnauer  *   */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
block|{
name|FIELD
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|value
operator|=
name|RUNTIME
argument_list|)
DECL|interface|SuperType
specifier|public
annotation_defn|@interface
name|SuperType
block|{
comment|/**      *       * @return the specified super type      */
DECL|method|superType
name|Class
name|superType
parameter_list|()
function_decl|;
block|}
end_annotation_defn

end_unit

