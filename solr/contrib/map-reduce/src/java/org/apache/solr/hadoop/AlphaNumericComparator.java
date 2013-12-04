begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//The MIT License
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Permission is hereby granted, free of charge, to any person obtaining a copy
end_comment

begin_comment
comment|// of this software and associated documentation files (the "Software"), to
end_comment

begin_comment
comment|// deal in the Software without restriction, including without limitation the
end_comment

begin_comment
comment|// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
end_comment

begin_comment
comment|// sell copies of the Software, and to permit persons to whom the Software is
end_comment

begin_comment
comment|// furnished to do so, subject to the following conditions:
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The above copyright notice and this permission notice shall be included in
end_comment

begin_comment
comment|// all copies or substantial portions of the Software.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
end_comment

begin_comment
comment|// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
end_comment

begin_comment
comment|// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
end_comment

begin_comment
comment|// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
end_comment

begin_comment
comment|// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
end_comment

begin_comment
comment|// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
end_comment

begin_comment
comment|// IN THE SOFTWARE.
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * This is a comparator to perform a mix of alphabetical+numeric comparison. For  * example, if there is a list {"test10", "test2", "test150", "test25", "test1"}  * then what we generally expect from the ordering is the result {"test1",  * "test2", "test10", "test25", "test150"}. However, standard lexigraphic  * ordering does not do that and "test10" comes before "test2". This class is  * provided to overcome that problem. This functionality is useful to sort the  * benchmark files (like the ones in in DL-benchmark-suite) from smallest to the  * largest. Comparisons are done on the String values retuned by toString() so  * care should be taken when this comparator is used to sort arbitrary Java  * objects.  *   */
end_comment

begin_class
DECL|class|AlphaNumericComparator
specifier|final
class|class
name|AlphaNumericComparator
implements|implements
name|Comparator
block|{
DECL|method|AlphaNumericComparator
specifier|public
name|AlphaNumericComparator
parameter_list|()
block|{     }
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|String
name|s1
init|=
name|o1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|o2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|n1
init|=
name|s1
operator|.
name|length
argument_list|()
decl_stmt|,
name|n2
init|=
name|s2
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|i1
init|=
literal|0
decl_stmt|,
name|i2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i1
operator|<
name|n1
operator|&&
name|i2
operator|<
name|n2
condition|)
block|{
name|int
name|p1
init|=
name|i1
decl_stmt|;
name|int
name|p2
init|=
name|i2
decl_stmt|;
name|char
name|c1
init|=
name|s1
operator|.
name|charAt
argument_list|(
name|i1
operator|++
argument_list|)
decl_stmt|;
name|char
name|c2
init|=
name|s2
operator|.
name|charAt
argument_list|(
name|i2
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c1
operator|!=
name|c2
condition|)
block|{
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|c1
argument_list|)
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|c2
argument_list|)
condition|)
block|{
name|int
name|value1
init|=
literal|0
decl_stmt|,
name|value2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i1
operator|<
name|n1
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|c1
operator|=
name|s1
operator|.
name|charAt
argument_list|(
name|i1
argument_list|)
argument_list|)
condition|)
block|{
name|i1
operator|++
expr_stmt|;
block|}
name|value1
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|s1
operator|.
name|substring
argument_list|(
name|p1
argument_list|,
name|i1
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|i2
operator|<
name|n2
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|c2
operator|=
name|s2
operator|.
name|charAt
argument_list|(
name|i2
argument_list|)
argument_list|)
condition|)
block|{
name|i2
operator|++
expr_stmt|;
block|}
name|value2
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|s2
operator|.
name|substring
argument_list|(
name|p2
argument_list|,
name|i2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|value1
operator|!=
name|value2
condition|)
block|{
return|return
name|value1
operator|-
name|value2
return|;
block|}
block|}
return|return
name|c1
operator|-
name|c2
return|;
block|}
block|}
return|return
name|n1
operator|-
name|n2
return|;
block|}
block|}
end_class

end_unit

