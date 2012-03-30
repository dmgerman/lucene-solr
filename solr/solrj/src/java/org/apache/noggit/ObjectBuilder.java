begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.noggit
package|package
name|org
operator|.
name|apache
operator|.
name|noggit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|ObjectBuilder
specifier|public
class|class
name|ObjectBuilder
block|{
DECL|method|fromJSON
specifier|public
specifier|static
name|Object
name|fromJSON
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|JSONParser
name|p
init|=
operator|new
name|JSONParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
return|return
name|getVal
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|getVal
specifier|public
specifier|static
name|Object
name|getVal
parameter_list|(
name|JSONParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ObjectBuilder
argument_list|(
name|parser
argument_list|)
operator|.
name|getVal
argument_list|()
return|;
block|}
DECL|field|parser
specifier|final
name|JSONParser
name|parser
decl_stmt|;
DECL|method|ObjectBuilder
specifier|public
name|ObjectBuilder
parameter_list|(
name|JSONParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|lastEvent
argument_list|()
operator|==
literal|0
condition|)
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
block|}
DECL|method|getVal
specifier|public
name|Object
name|getVal
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|lastEvent
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|ev
condition|)
block|{
case|case
name|JSONParser
operator|.
name|STRING
case|:
return|return
name|getString
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|LONG
case|:
return|return
name|getLong
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|NUMBER
case|:
return|return
name|getNumber
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|BIGNUMBER
case|:
return|return
name|getBigNumber
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|BOOLEAN
case|:
return|return
name|getBoolean
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|NULL
case|:
return|return
name|getNull
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
return|return
name|getObject
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|OBJECT_END
case|:
return|return
literal|null
return|;
comment|// OR ERROR?
case|case
name|JSONParser
operator|.
name|ARRAY_START
case|:
return|return
name|getArray
argument_list|()
return|;
case|case
name|JSONParser
operator|.
name|ARRAY_END
case|:
return|return
literal|null
return|;
comment|// OR ERROR?
case|case
name|JSONParser
operator|.
name|EOF
case|:
return|return
literal|null
return|;
comment|// OR ERROR?
default|default:
return|return
literal|null
return|;
comment|// OR ERROR?
block|}
block|}
DECL|method|getString
specifier|public
name|Object
name|getString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getString
argument_list|()
return|;
block|}
DECL|method|getLong
specifier|public
name|Object
name|getLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|parser
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getNumber
specifier|public
name|Object
name|getNumber
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArr
name|num
init|=
name|parser
operator|.
name|getNumberChars
argument_list|()
decl_stmt|;
name|String
name|numstr
init|=
name|num
operator|.
name|toString
argument_list|()
decl_stmt|;
name|double
name|d
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|numstr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|d
argument_list|)
condition|)
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|d
argument_list|)
return|;
comment|// TODO: use more efficient constructor in Java5
return|return
operator|new
name|BigDecimal
argument_list|(
name|numstr
argument_list|)
return|;
block|}
DECL|method|getBigNumber
specifier|public
name|Object
name|getBigNumber
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArr
name|num
init|=
name|parser
operator|.
name|getNumberChars
argument_list|()
decl_stmt|;
name|String
name|numstr
init|=
name|num
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ch
init|;
operator|(
name|ch
operator|=
name|num
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|;
control|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'e'
operator|||
name|ch
operator|==
literal|'E'
condition|)
return|return
operator|new
name|BigDecimal
argument_list|(
name|numstr
argument_list|)
return|;
block|}
return|return
operator|new
name|BigInteger
argument_list|(
name|numstr
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|public
name|Object
name|getBoolean
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getBoolean
argument_list|()
return|;
block|}
DECL|method|getNull
specifier|public
name|Object
name|getNull
parameter_list|()
throws|throws
name|IOException
block|{
name|parser
operator|.
name|getNull
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|newObject
specifier|public
name|Object
name|newObject
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|LinkedHashMap
argument_list|()
return|;
block|}
DECL|method|getKey
specifier|public
name|Object
name|getKey
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parser
operator|.
name|getString
argument_list|()
return|;
block|}
DECL|method|addKeyVal
specifier|public
name|void
name|addKeyVal
parameter_list|(
name|Object
name|map
parameter_list|,
name|Object
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|prev
init|=
operator|(
operator|(
name|Map
operator|)
name|map
operator|)
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
decl_stmt|;
comment|// TODO: test for repeated value?
block|}
DECL|method|objectEnd
specifier|public
name|Object
name|objectEnd
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
return|;
block|}
DECL|method|getObject
specifier|public
name|Object
name|getObject
parameter_list|()
throws|throws
name|IOException
block|{
name|Object
name|m
init|=
name|newObject
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
return|return
name|objectEnd
argument_list|(
name|m
argument_list|)
return|;
name|Object
name|key
init|=
name|getKey
argument_list|()
decl_stmt|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|Object
name|val
init|=
name|getVal
argument_list|()
decl_stmt|;
name|addKeyVal
argument_list|(
name|m
argument_list|,
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newArray
specifier|public
name|Object
name|newArray
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|()
return|;
block|}
DECL|method|addArrayVal
specifier|public
name|void
name|addArrayVal
parameter_list|(
name|Object
name|arr
parameter_list|,
name|Object
name|val
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|List
operator|)
name|arr
operator|)
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|endArray
specifier|public
name|Object
name|endArray
parameter_list|(
name|Object
name|arr
parameter_list|)
block|{
return|return
name|arr
return|;
block|}
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
throws|throws
name|IOException
block|{
name|Object
name|arr
init|=
name|newArray
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|ARRAY_END
condition|)
return|return
name|endArray
argument_list|(
name|arr
argument_list|)
return|;
name|Object
name|val
init|=
name|getVal
argument_list|()
decl_stmt|;
name|addArrayVal
argument_list|(
name|arr
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

