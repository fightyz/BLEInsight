# BLEInsight
该App能够搜索BLE的Peripheral设备，查看所提供的service，操作相关characteristic的值，能够输出一部分log到UI界面以便于调试。

这个App应该运行在Android 5.0之上，因为它使用了大量5.0的新特性，包括一些Materail Design：Recycler View, TabLayout, Toolbar, CoordinateLayout等等。

这个项目主要是用于自己学习Android BLE和Material Design相关开发的，不过功能上还是很齐全了。

之后有空可能会再补充新功能，包括：

1. 对每一个搜索到的BLE Peripheral设备，能够查看其广播信息。
2. 实现显示的bond
3. 能够让用户选择是否设置自动重连, autoConnect
4. UI中的调试log更加齐全
