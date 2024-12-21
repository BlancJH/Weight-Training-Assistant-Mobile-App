import 'package:flutter/material.dart';

class CustomListView extends StatelessWidget {
  final int itemCount;
  final Widget Function(BuildContext, int) itemBuilder;
  final EdgeInsets padding;
  final ScrollPhysics? physics; // Allow customisation of scroll physics
  final bool shrinkWrap; // Support wrapping inside another scrollable widget
  final Axis scrollDirection; // Allow vertical or horizontal scrolling
  final Widget? separator; // Optional separator between items
  final Widget? emptyWidget; // Widget to show when the list is empty

  const CustomListView({
    Key? key,
    required this.itemCount,
    required this.itemBuilder,
    this.padding = const EdgeInsets.all(16.0),
    this.physics,
    this.shrinkWrap = false,
    this.scrollDirection = Axis.vertical,
    this.separator,
    this.emptyWidget,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (itemCount == 0) {
      return emptyWidget ?? Center(child: Text('No items available'));
    }

    if (separator != null) {
      return ListView.separated(
        padding: padding,
        itemCount: itemCount,
        itemBuilder: itemBuilder,
        separatorBuilder: (_, __) => separator!,
        physics: physics,
        shrinkWrap: shrinkWrap,
        scrollDirection: scrollDirection,
      );
    }

    return ListView.builder(
      padding: padding,
      itemCount: itemCount,
      itemBuilder: itemBuilder,
      physics: physics,
      shrinkWrap: shrinkWrap,
      scrollDirection: scrollDirection,
    );
  }
}
