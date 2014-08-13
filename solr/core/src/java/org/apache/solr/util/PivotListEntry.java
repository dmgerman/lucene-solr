begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Enum for modeling the elements of a (nested) pivot entry as expressed in a NamedList  */
end_comment

begin_enum
DECL|enum|PivotListEntry
specifier|public
enum|enum
name|PivotListEntry
block|{
DECL|enum constant|FIELD
name|FIELD
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|VALUE
name|VALUE
argument_list|(
literal|1
argument_list|)
block|,
DECL|enum constant|COUNT
name|COUNT
argument_list|(
literal|2
argument_list|)
block|,
DECL|enum constant|PIVOT
name|PIVOT
argument_list|(
literal|3
argument_list|)
block|;
comment|// we could just use the ordinal(), but safer to be very explicit
DECL|field|index
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
DECL|method|PivotListEntry
specifier|private
name|PivotListEntry
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|/**    * Case-insensitive lookup of PivotListEntry by name    * @see #getName    */
DECL|method|get
specifier|public
specifier|static
name|PivotListEntry
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|PivotListEntry
operator|.
name|valueOf
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Name of this entry when used in response    * @see #get    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
comment|/**    * Indec of this entry when used in response    */
DECL|method|getIndex
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
block|}
end_enum

end_unit

