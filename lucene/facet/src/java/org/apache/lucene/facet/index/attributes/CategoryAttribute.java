begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.attributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|attributes
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|util
operator|.
name|Attribute
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An attribute which contains for a certain category the {@link CategoryPath}  * and additional properties.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|CategoryAttribute
specifier|public
interface|interface
name|CategoryAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Set the content of this {@link CategoryAttribute} from another    * {@link CategoryAttribute} object.    *     * @param other    *            The {@link CategoryAttribute} to take the content from.    */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|CategoryAttribute
name|other
parameter_list|)
function_decl|;
comment|/**    * Sets the category path value of this attribute.    *     * @param cp    *            A category path. May not be null.    */
DECL|method|setCategoryPath
specifier|public
name|void
name|setCategoryPath
parameter_list|(
name|CategoryPath
name|cp
parameter_list|)
function_decl|;
comment|/**    * Returns the value of this attribute: a category path.    *     * @return The category path last assigned to this attribute, or null if    *         none has been assigned.    */
DECL|method|getCategoryPath
specifier|public
name|CategoryPath
name|getCategoryPath
parameter_list|()
function_decl|;
comment|/**    * Add a property. The property can be later retrieved using    * {@link #getProperty(Class)} with this property class .<br>    * Adding multiple properties of the same class is forbidden.    *     * @param property    *            The property to add.    * @throws UnsupportedOperationException    *             When attempting to add a property of a class that was added    *             before and merge is prohibited.    */
DECL|method|addProperty
specifier|public
name|void
name|addProperty
parameter_list|(
name|CategoryProperty
name|property
parameter_list|)
throws|throws
name|UnsupportedOperationException
function_decl|;
comment|/**    * Get a property of a certain property class.    *     * @param propertyClass    *            The required property class.    * @return The property of the given class, or null if no such property    *         exists.    */
DECL|method|getProperty
specifier|public
name|CategoryProperty
name|getProperty
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
name|propertyClass
parameter_list|)
function_decl|;
comment|/**    * Get a property of one of given property classes.    *     * @param propertyClasses    *            The property classes.    * @return A property matching one of the given classes, or null if no such    *         property exists.    */
DECL|method|getProperty
specifier|public
name|CategoryProperty
name|getProperty
parameter_list|(
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
name|propertyClasses
parameter_list|)
function_decl|;
comment|/**    * Get all the active property classes.    *     * @return A set containing the active property classes, or {@code null} if    *         there are no properties.    */
DECL|method|getPropertyClasses
specifier|public
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
argument_list|>
name|getPropertyClasses
parameter_list|()
function_decl|;
comment|/**    * Clone this {@link CategoryAttribute}.    *     * @return A clone of this {@link CategoryAttribute}.    */
DECL|method|clone
specifier|public
name|CategoryAttribute
name|clone
parameter_list|()
function_decl|;
comment|/**    * Resets this attribute to its initial value: a null category path and no    * properties.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Clear all properties.    */
DECL|method|clearProperties
specifier|public
name|void
name|clearProperties
parameter_list|()
function_decl|;
comment|/**    * Remove an property of a certain property class.    *     * @param propertyClass    *            The required property class.    */
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
name|propertyClass
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

