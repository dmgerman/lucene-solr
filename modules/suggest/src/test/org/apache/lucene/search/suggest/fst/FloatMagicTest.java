begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|NumericUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|FloatMagicTest
specifier|public
class|class
name|FloatMagicTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testFloatMagic
specifier|public
name|void
name|testFloatMagic
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|floats
init|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Float
index|[]
block|{
name|Float
operator|.
name|intBitsToFloat
argument_list|(
literal|0x7f800001
argument_list|)
block|,
comment|// NaN (invalid combination).
name|Float
operator|.
name|intBitsToFloat
argument_list|(
literal|0x7fffffff
argument_list|)
block|,
comment|// NaN (invalid combination).
name|Float
operator|.
name|intBitsToFloat
argument_list|(
literal|0xff800001
argument_list|)
block|,
comment|// NaN (invalid combination).
name|Float
operator|.
name|intBitsToFloat
argument_list|(
literal|0xffffffff
argument_list|)
block|,
comment|// NaN (invalid combination).
name|Float
operator|.
name|POSITIVE_INFINITY
block|,
name|Float
operator|.
name|MAX_VALUE
block|,
literal|100f
block|,
literal|0f
block|,
literal|0.1f
block|,
name|Float
operator|.
name|MIN_VALUE
block|,
name|Float
operator|.
name|NaN
block|,
operator|-
literal|0.0f
block|,
operator|-
name|Float
operator|.
name|MIN_VALUE
block|,
operator|-
literal|0.1f
block|,
operator|-
literal|1f
block|,
operator|-
literal|10f
block|,
name|Float
operator|.
name|NEGATIVE_INFINITY
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|// Sort them using juc.
name|Collections
operator|.
name|sort
argument_list|(
name|floats
argument_list|)
expr_stmt|;
comment|// Convert to sortable int4 representation (as long to have an unsigned sort).
name|long
index|[]
name|int4
init|=
operator|new
name|long
index|[
name|floats
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|floats
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int4
index|[
name|i
index|]
operator|=
name|FloatMagic
operator|.
name|toSortable
argument_list|(
name|floats
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|&
literal|0xffffffffL
expr_stmt|;
comment|/*       System.out.println(           String.format("raw %8s sortable %8s %8s numutils %8s %s",               Integer.toHexString(Float.floatToRawIntBits(floats.get(i))),               Integer.toHexString(FloatMagic.toSortable(floats.get(i))),               Integer.toHexString(FloatMagic.unsignedOrderedToFloatBits(FloatMagic.toSortable(floats.get(i)))),               Integer.toHexString(NumericUtils.floatToSortableInt(floats.get(i))),               floats.get(i)));       */
block|}
comment|// Sort and compare. Should be identical order.
name|Arrays
operator|.
name|sort
argument_list|(
name|int4
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|backFromFixed
init|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|int4
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|backFromFixed
operator|.
name|add
argument_list|(
name|FloatMagic
operator|.
name|fromSortable
argument_list|(
operator|(
name|int
operator|)
name|int4
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*     for (int i = 0; i< int4.length; i++) {       System.out.println(           floats.get(i) + " " + FloatMagic.fromSortable((int) int4[i]));     }     */
name|assertEquals
argument_list|(
name|floats
argument_list|,
name|backFromFixed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Once checked, valid forever?"
argument_list|)
annotation|@
name|Test
DECL|method|testRoundTripFullRange
specifier|public
name|void
name|testRoundTripFullRange
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
do|do
block|{
name|float
name|f
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|float
name|f2
init|=
name|FloatMagic
operator|.
name|fromSortable
argument_list|(
name|FloatMagic
operator|.
name|toSortable
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|Float
operator|.
name|isNaN
argument_list|(
name|f
argument_list|)
operator|&&
name|Float
operator|.
name|isNaN
argument_list|(
name|f2
argument_list|)
operator|)
operator|||
name|f
operator|==
name|f2
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"! "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|i
argument_list|)
operator|+
literal|"> "
operator|+
name|f
operator|+
literal|" "
operator|+
name|f2
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|i
operator|&
literal|0xffffff
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
do|while
condition|(
name|i
operator|!=
literal|0
condition|)
do|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Once checked, valid forever?"
argument_list|)
annotation|@
name|Test
DECL|method|testIncreasingFullRange
specifier|public
name|void
name|testIncreasingFullRange
parameter_list|()
block|{
comment|// -infinity ... -0.0
for|for
control|(
name|int
name|i
init|=
literal|0xff800000
init|;
name|i
operator|!=
literal|0x80000000
condition|;
name|i
operator|--
control|)
block|{
name|checkSmaller
argument_list|(
name|i
argument_list|,
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// -0.0 +0.0
name|checkSmaller
argument_list|(
literal|0x80000000
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// +0.0 ... +infinity
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|!=
literal|0x7f800000
condition|;
name|i
operator|++
control|)
block|{
name|checkSmaller
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// All other are NaNs and should be after positive infinity.
specifier|final
name|long
name|infinity
init|=
name|toSortableL
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0x7f800001
init|;
name|i
operator|!=
literal|0x7fffffff
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|infinity
operator|<
name|toSortableL
argument_list|(
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0xff800001
init|;
name|i
operator|!=
literal|0xffffffff
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|infinity
operator|<
name|toSortableL
argument_list|(
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toSortableL
specifier|private
name|long
name|toSortableL
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|FloatMagic
operator|.
name|toSortable
argument_list|(
name|f
argument_list|)
operator|&
literal|0xffffffffL
return|;
block|}
DECL|method|checkSmaller
specifier|private
name|void
name|checkSmaller
parameter_list|(
name|int
name|i1
parameter_list|,
name|int
name|i2
parameter_list|)
block|{
name|float
name|f1
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|i1
argument_list|)
decl_stmt|;
name|float
name|f2
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|i2
argument_list|)
decl_stmt|;
if|if
condition|(
name|f1
operator|>
name|f2
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|f1
operator|+
literal|" "
operator|+
name|f2
operator|+
literal|" "
operator|+
name|i1
operator|+
literal|" "
operator|+
name|i2
argument_list|)
throw|;
block|}
name|assertTrue
argument_list|(
name|toSortableL
argument_list|(
name|f1
argument_list|)
operator|<
name|toSortableL
argument_list|(
name|f2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

