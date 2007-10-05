begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|AtomParser
specifier|public
interface|interface
name|AtomParser
block|{
comment|/** 	 * Error message for an unexpected element 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|UNEXPECTED_ELEMENT
specifier|public
specifier|static
specifier|final
name|String
name|UNEXPECTED_ELEMENT
init|=
literal|"Expected Element '%s' but was '%s' "
decl_stmt|;
comment|/** 	 * Error message for an unexpected element child 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|UNEXPECTED_ELEMENT_CHILD
specifier|public
specifier|static
specifier|final
name|String
name|UNEXPECTED_ELEMENT_CHILD
init|=
literal|"Element '%s' can not contain child elements "
decl_stmt|;
comment|/** 	 * Error message for an urecognized element child 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|URECOGNIZED_ELEMENT_CHILD
specifier|public
specifier|static
specifier|final
name|String
name|URECOGNIZED_ELEMENT_CHILD
init|=
literal|"Element '%s' can not contain child elements of the type %s"
decl_stmt|;
comment|/** 	 * Error message for an unexpected attribute 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|UNEXPECTED_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|UNEXPECTED_ATTRIBUTE
init|=
literal|"Element '%s' can not contain attributes "
decl_stmt|;
comment|/** 	 * Error message for an unexpected element value 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|UNEXPECTED_ELEMENT_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|UNEXPECTED_ELEMENT_VALUE
init|=
literal|"Element '%s' can not contain any element value"
decl_stmt|;
comment|/** 	 * Error message for a missing element attribute 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|MISSING_ELEMENT_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|MISSING_ELEMENT_ATTRIBUTE
init|=
literal|"Element '%s' requires an '%s' attribute"
decl_stmt|;
comment|/** 	 * Error message for a missing element child 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|MISSING_ELEMENT_CHILD
specifier|public
specifier|static
specifier|final
name|String
name|MISSING_ELEMENT_CHILD
init|=
literal|"Element '%s' requires a child of the type '%s'"
decl_stmt|;
comment|/** 	 * Error message for a missing element value 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|MISSING_ELEMENT_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|MISSING_ELEMENT_VALUE
init|=
literal|"Element '%s' requires a element value of the type '%s'"
decl_stmt|;
comment|/** 	 * Error message for a missing element value  	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|MISSING_ELEMENT_VALUE_PLAIN
specifier|public
specifier|static
specifier|final
name|String
name|MISSING_ELEMENT_VALUE_PLAIN
init|=
literal|"Element '%s' requires a element value'"
decl_stmt|;
comment|/** 	 * Error message for a duplicated element 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|DUPLICATE_ELEMENT
specifier|public
specifier|static
specifier|final
name|String
name|DUPLICATE_ELEMENT
init|=
literal|"Duplicated Element '%s'"
decl_stmt|;
comment|/** 	 * Error message for a duplicated element value 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|DUPLICATE_ELEMENT_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|DUPLICATE_ELEMENT_VALUE
init|=
literal|"Duplicated Element value for element '%s'"
decl_stmt|;
comment|/** 	 * Error message for a duplicated attribute 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|DUPLICATE_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|DUPLICATE_ATTRIBUTE
init|=
literal|"Duplicated Attribute '%s'"
decl_stmt|;
comment|/** 	 * Error message for an invalid attribute 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|INVALID_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|INVALID_ATTRIBUTE
init|=
literal|"The attribute '%s' must be an %s"
decl_stmt|;
comment|/** 	 * Error message for an invalid element value 	 * @see String#format(java.lang.String, java.lang.Object[]) 	 */
DECL|field|INVALID_ELEMENT_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|INVALID_ELEMENT_VALUE
init|=
literal|"The element value '%s' must be an %s"
decl_stmt|;
DECL|method|processElementValue
specifier|public
specifier|abstract
name|void
name|processElementValue
parameter_list|(
name|String
name|aValue
parameter_list|)
function_decl|;
DECL|method|processAttribute
specifier|public
specifier|abstract
name|void
name|processAttribute
parameter_list|(
name|QName
name|aQName
parameter_list|,
name|String
name|aValue
parameter_list|)
function_decl|;
DECL|method|processEndElement
specifier|public
specifier|abstract
name|void
name|processEndElement
parameter_list|()
function_decl|;
DECL|method|getChildParser
specifier|public
specifier|abstract
name|AtomParser
name|getChildParser
parameter_list|(
name|QName
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

