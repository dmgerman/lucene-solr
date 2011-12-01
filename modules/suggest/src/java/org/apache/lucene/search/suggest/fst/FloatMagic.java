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

begin_comment
comment|/**  * Converts normalized float representations ({@link Float#floatToIntBits(float)})  * into integers that are directly sortable in int4 representation (or unsigned values or  * after promoting to a long with higher 32-bits zeroed).  */
end_comment

begin_class
DECL|class|FloatMagic
class|class
name|FloatMagic
block|{
comment|/**    * Convert a float to a directly sortable unsigned integer. For sortable signed    * integers, see {@link NumericUtils#floatToSortableInt(float)}.    */
DECL|method|toSortable
specifier|public
specifier|static
name|int
name|toSortable
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|floatBitsToUnsignedOrdered
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Back from {@link #toSortable(float)} to float.    */
DECL|method|fromSortable
specifier|public
specifier|static
name|float
name|fromSortable
parameter_list|(
name|int
name|v
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|unsignedOrderedToFloatBits
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Convert float bits to directly sortable bits.     * Normalizes all NaNs to canonical form.    */
DECL|method|floatBitsToUnsignedOrdered
specifier|static
name|int
name|floatBitsToUnsignedOrdered
parameter_list|(
name|int
name|v
parameter_list|)
block|{
comment|// Canonicalize NaN ranges. I assume this check will be faster here than
comment|// (v == v) == false on the FPU? We don't distinguish between different
comment|// flavors of NaNs here (see http://en.wikipedia.org/wiki/NaN). I guess
comment|// in Java this doesn't matter much anyway.
if|if
condition|(
operator|(
name|v
operator|&
literal|0x7fffffff
operator|)
operator|>
literal|0x7f800000
condition|)
block|{
comment|// Apply the logic below to a canonical "quiet NaN"
return|return
literal|0x7fc00000
operator|^
literal|0x80000000
return|;
block|}
if|if
condition|(
name|v
operator|<
literal|0
condition|)
block|{
comment|// Reverse the order of negative values and push them before positive values.
return|return
operator|~
name|v
return|;
block|}
else|else
block|{
comment|// Shift positive values after negative, but before NaNs, they're sorted already.
return|return
name|v
operator|^
literal|0x80000000
return|;
block|}
block|}
comment|/**    * Back from {@link #floatBitsToUnsignedOrdered(int)}.    */
DECL|method|unsignedOrderedToFloatBits
specifier|static
name|int
name|unsignedOrderedToFloatBits
parameter_list|(
name|int
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|<
literal|0
condition|)
return|return
name|v
operator|&
operator|~
literal|0x80000000
return|;
else|else
return|return
operator|~
name|v
return|;
block|}
block|}
end_class

end_unit

