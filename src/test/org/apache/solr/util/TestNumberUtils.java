begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|NumberUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|BCDUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestNumberUtils
specifier|public
class|class
name|TestNumberUtils
extends|extends
name|TestCase
block|{
DECL|method|arrstr
specifier|private
specifier|static
name|String
name|arrstr
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|String
name|str
init|=
literal|"["
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|str
operator|+=
name|arr
index|[
name|i
index|]
operator|+
literal|"("
operator|+
operator|(
name|int
operator|)
name|arr
index|[
name|i
index|]
operator|+
literal|"),"
expr_stmt|;
return|return
name|str
operator|+
literal|"]"
return|;
block|}
DECL|field|rng
specifier|static
name|Random
name|rng
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|special
specifier|static
name|int
index|[]
name|special
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|100
block|,
literal|1000
block|,
literal|10000
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
name|Integer
operator|.
name|MIN_VALUE
block|}
decl_stmt|;
DECL|method|getSpecial
specifier|static
name|int
name|getSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|int
name|j
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|j
return|;
return|return
name|special
index|[
operator|(
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|special
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|field|lspecial
specifier|static
name|long
index|[]
name|lspecial
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|100
block|,
literal|1000
block|,
literal|10000
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|256
block|,
literal|16384
block|,
literal|32768
block|,
literal|65536
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
name|Integer
operator|.
name|MIN_VALUE
block|,
name|Long
operator|.
name|MAX_VALUE
block|,
name|Long
operator|.
name|MIN_VALUE
block|}
decl_stmt|;
DECL|method|getLongSpecial
specifier|static
name|long
name|getLongSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|j
init|=
name|rng
operator|.
name|nextLong
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|j
return|;
return|return
name|lspecial
index|[
operator|(
operator|(
name|int
operator|)
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|special
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|field|fspecial
specifier|static
name|float
index|[]
name|fspecial
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|256
block|,
literal|16384
block|,
literal|32768
block|,
literal|65536
block|,
literal|.1f
block|,
literal|.25f
block|,
name|Float
operator|.
name|NEGATIVE_INFINITY
block|,
name|Float
operator|.
name|POSITIVE_INFINITY
block|,
name|Float
operator|.
name|MIN_VALUE
block|,
name|Float
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
DECL|method|getFloatSpecial
specifier|static
name|float
name|getFloatSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|int
name|j
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|float
name|f
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
name|f
condition|)
name|f
operator|=
literal|0
expr_stmt|;
comment|// get rid of NaN for comparison purposes
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|f
return|;
return|return
name|fspecial
index|[
operator|(
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|fspecial
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|field|dspecial
specifier|static
name|double
index|[]
name|dspecial
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|256
block|,
literal|16384
block|,
literal|32768
block|,
literal|65536
block|,
literal|.1
block|,
literal|.25
block|,
name|Float
operator|.
name|NEGATIVE_INFINITY
block|,
name|Float
operator|.
name|POSITIVE_INFINITY
block|,
name|Float
operator|.
name|MIN_VALUE
block|,
name|Float
operator|.
name|MAX_VALUE
block|,
name|Double
operator|.
name|NEGATIVE_INFINITY
block|,
name|Double
operator|.
name|POSITIVE_INFINITY
block|,
name|Double
operator|.
name|MIN_VALUE
block|,
name|Double
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
DECL|method|getDoubleSpecial
specifier|static
name|double
name|getDoubleSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|j
init|=
name|rng
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|double
name|f
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
name|f
condition|)
name|f
operator|=
literal|0
expr_stmt|;
comment|// get rid of NaN for comparison purposes
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|f
return|;
return|return
name|dspecial
index|[
operator|(
operator|(
name|int
operator|)
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|dspecial
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|(
name|Comparable
name|n1
parameter_list|,
name|Comparable
name|n2
parameter_list|,
name|Converter
name|conv
parameter_list|)
block|{
name|String
name|s1
init|=
name|n1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|n2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|v1
init|=
name|conv
operator|.
name|toInternal
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|String
name|v2
init|=
name|conv
operator|.
name|toInternal
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|String
name|out1
init|=
name|conv
operator|.
name|toExternal
argument_list|(
name|v1
argument_list|)
decl_stmt|;
name|String
name|out2
init|=
name|conv
operator|.
name|toExternal
argument_list|(
name|v2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conv
operator|+
literal|" :: n1 :: input!=output"
argument_list|,
name|s1
argument_list|,
name|out1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conv
operator|+
literal|" :: n2 :: input!=output"
argument_list|,
name|s2
argument_list|,
name|out2
argument_list|)
expr_stmt|;
name|int
name|c1
init|=
name|n1
operator|.
name|compareTo
argument_list|(
name|n2
argument_list|)
decl_stmt|;
name|int
name|c2
init|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
operator|(
name|c1
operator|==
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|==
literal|0
operator|)
operator|)
argument_list|)
expr_stmt|;
comment|//    Assert.assertFalse( c1< 0&& !(c2<0) );
comment|//    Assert.assertFalse( c1> 0&& !(c2>0) );
comment|//
comment|//    if (c1==0&& !(c2==0)
comment|//    || c1< 0&& !(c2<0)
comment|//    || c1> 0&& !(c2>0)
comment|//    || !out1.equals(s1) || !out2.equals(s2))
comment|//    {
comment|//      Assert.fail("Comparison error:"+s1+","+s2 + " :: " + conv);
comment|//      System.out.print("v1=");
comment|//      for (int ii=0; ii<v1.length(); ii++) {
comment|//        System.out.print(" " + (int)v1.charAt(ii));
comment|//      }
comment|//      System.out.print("\nv2=");
comment|//      for (int ii=0; ii<v2.length(); ii++) {
comment|//        System.out.print(" " + (int)v2.charAt(ii));
comment|//      }
comment|//      System.out.println("\nout1='"+out1+"', out2='" + out2 + "'");
comment|//    }
block|}
DECL|method|testConverters
specifier|public
name|void
name|testConverters
parameter_list|()
block|{
name|int
name|iter
init|=
literal|1000
decl_stmt|;
name|int
name|arrsz
init|=
literal|100000
decl_stmt|;
name|int
name|num
init|=
literal|12345
decl_stmt|;
comment|// INTEGERS
name|List
argument_list|<
name|Converter
argument_list|>
name|converters
init|=
operator|new
name|ArrayList
argument_list|<
name|Converter
argument_list|>
argument_list|()
decl_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|Int2Int
argument_list|()
argument_list|)
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|SortInt
argument_list|()
argument_list|)
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|Base10kS
argument_list|()
argument_list|)
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|Base100S
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Converter
name|c
range|:
name|converters
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|Comparable
name|n1
init|=
name|getSpecial
argument_list|()
decl_stmt|;
name|Comparable
name|n2
init|=
name|getSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LONG
name|converters
operator|.
name|clear
argument_list|()
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|SortLong
argument_list|()
argument_list|)
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|Base10kS
argument_list|()
argument_list|)
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|Base100S
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Converter
name|c
range|:
name|converters
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|Comparable
name|n1
init|=
name|getLongSpecial
argument_list|()
decl_stmt|;
name|Comparable
name|n2
init|=
name|getLongSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// FLOAT
name|converters
operator|.
name|clear
argument_list|()
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|Float2Float
argument_list|()
argument_list|)
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|SortFloat
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Converter
name|c
range|:
name|converters
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|Comparable
name|n1
init|=
name|getFloatSpecial
argument_list|()
decl_stmt|;
name|Comparable
name|n2
init|=
name|getFloatSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// DOUBLE
name|converters
operator|.
name|clear
argument_list|()
expr_stmt|;
name|converters
operator|.
name|add
argument_list|(
operator|new
name|SortDouble
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Converter
name|c
range|:
name|converters
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|Comparable
name|n1
init|=
name|getDoubleSpecial
argument_list|()
decl_stmt|;
name|Comparable
name|n2
init|=
name|getDoubleSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_class
DECL|class|Converter
specifier|abstract
class|class
name|Converter
block|{
DECL|method|toInternal
specifier|abstract
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
function_decl|;
DECL|method|toExternal
specifier|abstract
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
function_decl|;
block|}
end_class

begin_class
DECL|class|Int2Int
class|class
name|Int2Int
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|SortInt
class|class
name|SortInt
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|int2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2int
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|SortLong
class|class
name|SortLong
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|long2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|Float2Float
class|class
name|Float2Float
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|SortFloat
class|class
name|SortFloat
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|float2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2floatStr
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|SortDouble
class|class
name|SortDouble
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|double2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2doubleStr
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|Base100S
class|class
name|Base100S
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base10toBase100SortableInt
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base100SortableIntToBase10
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|Base10kS
class|class
name|Base10kS
extends|extends
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base10toBase10kSortableInt
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base10kSortableIntToBase10
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class

end_unit

