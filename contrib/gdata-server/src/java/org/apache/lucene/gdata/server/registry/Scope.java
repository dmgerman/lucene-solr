begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * The Scope annotation is used in  * {@link org.apache.lucene.gdata.server.registry.ScopeVisitable} and  * {@link org.apache.lucene.gdata.server.registry.ScopeVisitor} implementations  * to indicate which scope should be visited.  *   * @author Simon Willnauer  *   */
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
DECL|interface|Scope
specifier|public
annotation_defn|@interface
name|Scope
block|{
comment|/**      * @return - the scope type the class was annotated with      */
DECL|method|scope
name|ScopeType
name|scope
parameter_list|()
function_decl|;
comment|/**      * Defines a Scope for {@link Scope} annotations      *       * @author Simon Willnauer      *       */
DECL|enum|ScopeType
specifier|public
specifier|static
enum|enum
name|ScopeType
block|{
comment|/**          * Request scope          */
DECL|enum constant|REQUEST
name|REQUEST
block|,
comment|/**          * Session scope          */
DECL|enum constant|SESSION
name|SESSION
block|,
comment|/**          * Context scope          */
DECL|enum constant|CONTEXT
name|CONTEXT
block|}
block|}
end_annotation_defn

end_unit

